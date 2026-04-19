package org.garvsurve.auditable.rules;

import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * FormLabelRule — WCAG 1.3.1 (Info and Relationships) & 3.3.2 (Labels or Instructions)
 *
 * Verifies that every interactive form control ({@code <input>},
 * {@code <textarea>}, {@code <select>}) has a programmatically associated
 * label so that assistive technologies can announce the field's purpose.
 *
 * <p>Checks performed:
 * <ol>
 *   <li>A {@code <label for="…">} element exists whose value matches the
 *       control's {@code id} attribute.</li>
 *   <li>The control is not relying solely on a {@code placeholder} attribute
 *       for its label — placeholders disappear on input and are not announced
 *       consistently by all screen readers.</li>
 *   <li>Hidden inputs ({@code type="hidden"}) and submit/reset/button inputs
 *       are excluded because they do not require visible labels.</li>
 * </ol>
 */
@Slf4j
@Component
public class FormLabelRule implements Rule {

    /** Input types that do not require a visible label. */
    private static final Set<String> EXEMPT_INPUT_TYPES = Set.of(
            "hidden", "submit", "reset", "button", "image"
    );

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element control : doc.select("input, textarea, select")) {
            // Skip input types that don't need a label
            if (control.tagName().equals("input")) {
                String type = control.attr("type").toLowerCase();
                if (EXEMPT_INPUT_TYPES.contains(type)) {
                    continue;
                }
            }

            String controlId    = control.attr("id").trim();
            boolean hasAriaLabel = hasNonEmptyAttr(control, "aria-label")
                                || hasNonEmptyAttr(control, "aria-labelledby");

            // aria-label / aria-labelledby are valid programmatic labels — skip
            if (hasAriaLabel) {
                continue;
            }

            boolean hasAssociatedLabel = false;

            if (!controlId.isEmpty()) {
                // Look for a <label for="controlId"> anywhere in the document
                Element label = doc.selectFirst("label[for=" + controlId + "]");
                hasAssociatedLabel = label != null
                        && !label.text().trim().isEmpty();
            }

            if (!hasAssociatedLabel) {
                boolean hasPlaceholderOnly = hasNonEmptyAttr(control, "placeholder") && controlId.isEmpty();

                if (hasPlaceholderOnly) {
                    // Placeholder present but no proper label — common antipattern
                    issues.add(Issue.builder()
                            .type("[WCAG 1.3.1, 3.3.2] Placeholder-Only Label")
                            .wcag("1.3.1, 3.3.2")
                            .message("[WCAG 1.3.1, 3.3.2] Form control uses only a placeholder instead of a " +
                                     "<label> element. Placeholders disappear when the user types and are not " +
                                     "reliably announced by all screen readers.")
                            .severity(Severity.MEDIUM)
                            .category(Category.FORMS)
                            .element(truncate(control.outerHtml()))
                            .suggestion("Add a visible <label for=\"controlId\"> associated with this field. " +
                                        "Keep the placeholder as a hint, not a replacement for the label.")
                            .build());
                } else {
                    // No label, no placeholder, no aria attribute — fully unlabelled
                    issues.add(Issue.builder()
                            .type("[WCAG 1.3.1, 3.3.2] Missing Form Label")
                            .wcag("1.3.1, 3.3.2")
                            .message("[WCAG 1.3.1, 3.3.2] Form control <" + control.tagName() + "> has no " +
                                     "associated <label>, aria-label, or aria-labelledby attribute. " +
                                     (controlId.isEmpty()
                                             ? "The element also has no id, so a label[for] cannot reference it."
                                             : "No <label for=\"" + controlId + "\"> was found in the document."))
                            .severity(Severity.HIGH)
                            .category(Category.FORMS)
                            .element(truncate(control.outerHtml()))
                            .suggestion("Add <label for=\"" + (controlId.isEmpty() ? "FIELD_ID" : controlId) +
                                        "\">Descriptive Label</label> and ensure the control's id matches the " +
                                        "label's for attribute. Alternatively, use aria-label=\"Descriptive text\" " +
                                        "directly on the control.")
                            .build());
                }
            }
        }

        log.debug("FormLabelRule found {} issues", issues.size());
        return issues;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Returns {@code true} when the element has the attribute and it is not blank. */
    private boolean hasNonEmptyAttr(Element el, String attr) {
        return el.hasAttr(attr) && !el.attr(attr).trim().isEmpty();
    }

    private String truncate(String html) {
        return html.length() > 300 ? html.substring(0, 300) + "..." : html;
    }
}
