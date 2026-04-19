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

/**
 * ButtonAccessibilityRule — WCAG 4.1.2 (Name, Role, Value)
 *
 * Every {@code <button>} element must expose a non-empty accessible name so
 * that assistive technologies can announce what the button does.
 *
 * <p>An accessible name is considered present when ANY of the following is
 * true:
 * <ul>
 *   <li>The button has non-empty visible text content (excluding child
 *       elements that are hidden via {@code aria-hidden="true"}).</li>
 *   <li>The button has a non-empty {@code aria-label} attribute.</li>
 *   <li>The button has an {@code aria-labelledby} attribute that references
 *       a non-empty element in the document.</li>
 *   <li>The button has a non-empty {@code title} attribute (last resort,
 *       but technically valid).</li>
 * </ul>
 *
 * <p>Icon-only buttons (those whose only child is an {@code <svg>}, {@code <i>},
 * or {@code <img>} without alternative text) are flagged with a dedicated
 * message to guide the developer toward the most appropriate fix.
 */
@Slf4j
@Component
public class ButtonAccessibilityRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element button : doc.select("button")) {
            // Collect the visible text, ignoring aria-hidden descendants
            String visibleText = visibleText(button).trim();

            boolean hasAriaLabel      = hasNonEmptyAttr(button, "aria-label");
            boolean hasAriaLabelledBy = resolveAriaLabelledBy(doc, button);
            boolean hasTitle          = hasNonEmptyAttr(button, "title");

            // The button is accessible if any of these channels provide a name
            if (!visibleText.isEmpty() || hasAriaLabel || hasAriaLabelledBy || hasTitle) {
                continue;
            }

            // Determine whether the button appears to be icon-only
            boolean isIconOnly = isIconOnlyButton(button);

            if (isIconOnly) {
                issues.add(Issue.builder()
                        .type("[WCAG 4.1.2] Icon-Only Button Without Accessible Name")
                        .wcag("4.1.2")
                        .message("[WCAG 4.1.2] Button appears to be icon-only (contains <svg>, <i>, or <img> " +
                                 "without alt text) and has no aria-label, aria-labelledby, or title. " +
                                 "Screen reader users will hear only \"button\" with no indication of its purpose.")
                        .severity(Severity.HIGH)
                        .category(Category.STRUCTURE)
                        .element(truncate(button.outerHtml()))
                        .suggestion("Add aria-label=\"Descriptive action\" to the <button> element, e.g. " +
                                    "aria-label=\"Close dialog\". If an <svg> is used as the icon, also add " +
                                    "aria-hidden=\"true\" to the SVG so it is not announced separately.")
                        .build());
            } else {
                issues.add(Issue.builder()
                        .type("[WCAG 4.1.2] Empty Button")
                        .wcag("4.1.2")
                        .message("[WCAG 4.1.2] Button element has no visible text, aria-label, " +
                                 "aria-labelledby, or title attribute. Assistive technologies cannot " +
                                 "announce its purpose.")
                        .severity(Severity.HIGH)
                        .category(Category.STRUCTURE)
                        .element(truncate(button.outerHtml()))
                        .suggestion("Provide a visible text label inside the <button>, or add an " +
                                    "aria-label=\"Descriptive action\" attribute that describes what " +
                                    "pressing the button will do.")
                        .build());
            }
        }

        log.debug("ButtonAccessibilityRule found {} issues", issues.size());
        return issues;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the concatenated text content of the element, excluding any
     * descendant elements that carry {@code aria-hidden="true"}.
     */
    private String visibleText(Element element) {
        // Clone so we don't mutate the parsed tree
        Element clone = element.clone();
        // Remove aria-hidden children — they must not contribute to the name
        clone.select("[aria-hidden=true]").remove();
        return clone.text();
    }

    /**
     * Attempts to resolve {@code aria-labelledby} by looking up the referenced
     * element ids in the document and checking they have non-whitespace text.
     *
     * @return {@code true} if at least one referenced element has non-empty text.
     */
    private boolean resolveAriaLabelledBy(Document doc, Element button) {
        String labelledBy = button.attr("aria-labelledby").trim();
        if (labelledBy.isEmpty()) {
            return false;
        }
        for (String id : labelledBy.split("\\s+")) {
            Element referenced = doc.getElementById(id);
            if (referenced != null && !referenced.text().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Heuristically determines whether a button is "icon-only":
     * it contains an {@code <svg>}, {@code <i>}, or {@code <img>} (without
     * meaningful alt text) and has no other non-whitespace text nodes of its
     * own (outside those icon elements).
     */
    private boolean isIconOnlyButton(Element button) {
        boolean hasIconChild = !button.select("svg, i, span.icon, img").isEmpty();
        if (!hasIconChild) {
            return false;
        }

        // Check if <img> children lack meaningful alt text
        for (Element img : button.select("img")) {
            String alt = img.attr("alt").trim();
            if (!alt.isEmpty()) {
                return false; // img has alt text → it contributes an accessible name
            }
        }

        // Strip icon children and see if any text remains
        Element stripped = button.clone();
        stripped.select("svg, i, span.icon, img").remove();
        return stripped.text().trim().isEmpty();
    }

    /** Returns {@code true} when the element has the attribute and it is not blank. */
    private boolean hasNonEmptyAttr(Element el, String attr) {
        return el.hasAttr(attr) && !el.attr(attr).trim().isEmpty();
    }

    private String truncate(String html) {
        return html.length() > 300 ? html.substring(0, 300) + "..." : html;
    }
}
