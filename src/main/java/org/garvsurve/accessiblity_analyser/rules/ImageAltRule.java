package org.garvsurve.accessiblity_analyser.rules;

import org.garvsurve.accessiblity_analyser.model.Issue;

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
                        .type("Image Alt Missing")
                        .message("Image missing alt attribute")
                        .severity("HIGH")
                        .element(img.outerHtml())
                        .build());
            }
        }

        return issues;
    }
}