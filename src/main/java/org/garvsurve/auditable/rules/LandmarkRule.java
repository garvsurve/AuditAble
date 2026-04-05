package org.garvsurve.auditable.rules;

import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LandmarkRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        checkLandmark(doc, issues,
                "header, [role='banner']",
                "Missing Header Landmark",
                "No <header> element or role=\"banner\" found. " +
                "Add <header> to wrap your site logo, navigation, and title.",
                Severity.MEDIUM);

        checkLandmark(doc, issues,
                "nav, [role='navigation']",
                "Missing Navigation Landmark",
                "No <nav> element or role=\"navigation\" found. " +
                "Wrap your primary navigation links inside <nav>.",
                Severity.MEDIUM);

        checkLandmark(doc, issues,
                "main, [role='main']",
                "Missing Main Landmark",
                "No <main> element or role=\"main\" found. " +
                "Wrap the primary content of the page in <main>. " +
                "There should be exactly one <main> per page.",
                Severity.HIGH);

        checkLandmark(doc, issues,
                "footer, [role='contentinfo']",
                "Missing Footer Landmark",
                "No <footer> element or role=\"contentinfo\" found. " +
                "Add <footer> to mark site-wide footer content (copyright, contact links, etc.).",
                Severity.LOW);

        log.debug("LandmarkRule found {} issues", issues.size());
        return issues;
    }

    private void checkLandmark(Document doc, List<Issue> issues,
                                String selector, String type, String suggestion, Severity severity) {
        if (doc.select(selector).isEmpty()) {
            issues.add(Issue.builder()
                    .type(type)
                    .message(suggestion)
                    .severity(severity)
                    .category(Category.STRUCTURE)
                    .element("<body>...</body>")
                    .suggestion(suggestion)
                    .build());
        }
    }
}
