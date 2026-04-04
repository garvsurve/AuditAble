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
public class LinkTextRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element link : doc.select("a[href]")) {
            boolean hasText = !link.text().trim().isEmpty();
            boolean hasAriaLabel = link.hasAttr("aria-label") && !link.attr("aria-label").trim().isEmpty();
            boolean hasImageWithAlt = false;

            for (Element img : link.select("img")) {
                if (img.hasAttr("alt") && !img.attr("alt").trim().isEmpty()) {
                    hasImageWithAlt = true;
                    break;
                }
            }

            if (!hasText && !hasAriaLabel && !hasImageWithAlt) {
                issues.add(Issue.builder()
                        .type("Empty Link")
                        .message("Link contains no readable text or image with alt text")
                        .severity(Severity.HIGH)
                        .category(Category.LINKS)
                        .element(link.outerHtml())
                        .build());
            }
        }

        return issues;
    }
}
