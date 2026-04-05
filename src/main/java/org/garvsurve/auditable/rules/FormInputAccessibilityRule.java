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

@Slf4j
@Component
public class FormInputAccessibilityRule implements Rule {

    private static final String EXCLUDED_TYPES =
            "input:not([type='hidden']):not([type='submit'])" +
            ":not([type='button']):not([type='image']):not([type='reset'])";

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element input : doc.select(EXCLUDED_TYPES)) {
            String id = input.id();

            boolean hasAriaLabel      = hasNonBlankAttr(input, "aria-label");
            boolean hasAriaLabelledBy = input.hasAttr("aria-labelledby");
            boolean hasPlaceholder    = hasNonBlankAttr(input, "placeholder");
            boolean hasDirectLabel    = !id.isEmpty() && !doc.select("label[for='" + id + "']").isEmpty();
            boolean isWrappedInLabel  = input.parent() != null
                                        && "label".equalsIgnoreCase(input.parent().tagName());

            if (!hasAriaLabel && !hasAriaLabelledBy && !hasPlaceholder
                    && !hasDirectLabel && !isWrappedInLabel) {

                issues.add(Issue.builder()
                        .type("Form Input Inaccessible")
                        .message("Input element of type '" + inputType(input) +
                                 "' has no aria-label, placeholder, or associated <label>.")
                        .severity(Severity.HIGH)
                        .category(Category.FORMS)
                        .element(truncate(input.outerHtml()))
                        .suggestion("Add a <label for=\"inputId\"> or aria-label attribute so screen readers " +
                                    "can announce the input's purpose.")
                        .build());
            }
        }

        log.debug("FormInputAccessibilityRule found {} issues", issues.size());
        return issues;
    }

    private boolean hasNonBlankAttr(Element el, String attr) {
        return el.hasAttr(attr) && !el.attr(attr).trim().isEmpty();
    }

    private String inputType(Element el) {
        String t = el.attr("type");
        return t.isEmpty() ? "text" : t;
    }

    private String truncate(String html) {
        return html.length() > 300 ? html.substring(0, 300) + "..." : html;
    }
}
