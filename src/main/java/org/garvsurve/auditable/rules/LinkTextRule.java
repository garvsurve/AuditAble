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
                        .type("[WCAG 2.4.4] Empty Link")
                        .wcag("2.4.4")
                        .message("[WCAG 2.4.4] Link contains no readable text or image with alt text")
                        .severity(Severity.HIGH)
                        .category(Category.LINKS)
                        .element(link.outerHtml())
                        .suggestion("Add descriptive text inside <a> or use aria-label=\"Destination description\". " +
                                    "If the link contains only an image, add alt text to that image.")
                        .build());
            }
        }

        return issues;
    }
}
