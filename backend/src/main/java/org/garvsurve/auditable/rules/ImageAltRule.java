package org.garvsurve.auditable.rules;

import org.garvsurve.auditable.model.Issue;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImageAltRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element img : doc.select("img")) {
            if (!img.hasAttr("alt") || img.attr("alt").isEmpty()) {
                issues.add(Issue.builder()
                        .type("[WCAG 1.1.1] Image Alt Missing")
                        .wcag("1.1.1")
                        .message("[WCAG 1.1.1] Image missing alt attribute")
                        .severity(org.garvsurve.auditable.model.Severity.HIGH)
                        .category(org.garvsurve.auditable.model.Category.IMAGES)
                        .element(img.outerHtml())
                        .suggestion("Add alt=\"description\" to every meaningful image. " +
                                    "Use alt=\"\" (empty) for decorative images.")
                        .build());
            }
        }

        return issues;
    }
}