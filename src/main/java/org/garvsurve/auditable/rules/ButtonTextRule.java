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
public class ButtonTextRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element button : doc.select("button")) {
            boolean hasText = !button.text().trim().isEmpty();
            boolean hasAriaLabel = button.hasAttr("aria-label") && !button.attr("aria-label").trim().isEmpty();

            if (!hasText && !hasAriaLabel) {
                issues.add(Issue.builder()
                        .type("Empty Button")
                        .message("Button is empty or missing discernible text")
                        .severity(Severity.HIGH)
                        .category(Category.FORMS)
                        .element(button.outerHtml())
                        .build());
            }
        }

        return issues;
    }
}
