package org.garvsurve.auditable.rules;

import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InputLabelRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element input : doc.select("input:not([type='hidden']):not([type='submit']):not([type='button']):not([type='image'])")) {
            String id = input.id();
            boolean hasAriaLabel = input.hasAttr("aria-label") && !input.attr("aria-label").trim().isEmpty();
            boolean hasAriaLabelledBy = input.hasAttr("aria-labelledby");
            boolean hasDirectLabel = !id.isEmpty() && !doc.select("label[for='" + id + "']").isEmpty();
            boolean isWrappedInLabel = input.parent() != null && input.parent().tagName().equalsIgnoreCase("label");

            if (!hasAriaLabel && !hasAriaLabelledBy && !hasDirectLabel && !isWrappedInLabel) {
                issues.add(Issue.builder()
                        .type("[WCAG 1.3.1, 3.3.2] Input Label Missing")
                        .wcag("1.3.1, 3.3.2")
                        .message("[WCAG 1.3.1, 3.3.2] Input element is missing an associated label")
                        .severity(Severity.HIGH)
                        .category(Category.FORMS)
                        .element(input.outerHtml())
                        .suggestion("Add <label for=\"inputId\">Label text</label> and ensure the input has a " +
                                    "matching id, or use aria-label directly on the element.")
                        .build());
            }
        }

        return issues;
    }
}
