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
public class HeadingStructureRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();
        int prevLevel = 0;

        for (Element heading : doc.select("h1, h2, h3, h4, h5, h6")) {
            int currentLevel = Integer.parseInt(heading.tagName().substring(1));
            
            if (prevLevel > 0 && currentLevel - prevLevel > 1) {
                issues.add(Issue.builder()
                        .type("Skipped Heading Level")
                        .message("Heading level skips from H" + prevLevel + " to H" + currentLevel)
                        .severity(Severity.MEDIUM)
                        .category(Category.STRUCTURE)
                        .element(heading.outerHtml())
                        .build());
            }
            prevLevel = currentLevel;
        }

        return issues;
    }
}
