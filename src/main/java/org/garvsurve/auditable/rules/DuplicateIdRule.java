package org.garvsurve.auditable.rules;

import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DuplicateIdRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        Map<String, List<Element>> idMap = new HashMap<>();
        for (Element el : doc.select("[id]")) {
            String id = el.id().trim();
            if (!id.isEmpty()) {
                idMap.computeIfAbsent(id, k -> new ArrayList<>()).add(el);
            }
        }

        for (Map.Entry<String, List<Element>> entry : idMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                String duplicateId = entry.getKey();
                int count = entry.getValue().size();

                issues.add(Issue.builder()
                        .type("Duplicate ID")
                        .message("The id=\"" + duplicateId + "\" appears " + count + " times in the document. IDs must be unique.")
                        .severity(Severity.HIGH)
                        .category(Category.STRUCTURE)
                        .element(truncate(entry.getValue().get(0).outerHtml()))
                        .suggestion("Ensure every id attribute value is unique per page. " +
                                    "Rename duplicates to unique identifiers and update any aria-labelledby, " +
                                    "aria-describedby, and label[for] references accordingly.")
                        .build());
            }
        }

        log.debug("DuplicateIdRule found {} issues", issues.size());
        return issues;
    }

    private String truncate(String html) {
        return html.length() > 300 ? html.substring(0, 300) + "..." : html;
    }
}
