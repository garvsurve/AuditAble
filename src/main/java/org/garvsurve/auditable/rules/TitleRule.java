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
public class TitleRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();
        
        Element title = doc.selectFirst("title");
        if (title == null || title.text().trim().isEmpty()) {
            issues.add(Issue.builder()
                    .type("Missing Title")
                    .message("The webpage is missing a <title> tag or it is empty")
                    .severity(Severity.HIGH)
                    .category(Category.STRUCTURE)
                    .element(title == null ? "<head>" : title.outerHtml())
                    .build());
        }

        return issues;
    }
}
