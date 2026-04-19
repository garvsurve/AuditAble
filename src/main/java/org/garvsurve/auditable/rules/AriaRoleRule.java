package org.garvsurve.auditable.rules;

import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AriaRoleRule implements Rule {

    private static final List<String> VALID_ROLES = List.of(
            "alert", "alertdialog", "application", "article", "banner",
            "button", "cell", "checkbox", "columnheader", "combobox",
            "complementary", "contentinfo", "definition", "dialog",
            "directory", "document", "feed", "figure", "form", "grid",
            "gridcell", "group", "heading", "img", "link", "list",
            "listbox", "listitem", "log", "main", "marquee", "math",
            "menu", "menubar", "menuitem", "menuitemcheckbox",
            "menuitemradio", "navigation", "none", "note", "option",
            "presentation", "progressbar", "radio", "radiogroup", "region",
            "row", "rowgroup", "rowheader", "scrollbar", "search",
            "searchbox", "separator", "slider", "spinbutton", "status",
            "switch", "tab", "table", "tablist", "tabpanel", "term",
            "textbox", "timer", "toolbar", "tooltip", "tree", "treegrid",
            "treeitem"
    );

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element el : doc.select("[role]")) {
            String role = el.attr("role").trim().toLowerCase();

            if (role.isEmpty()) {
                issues.add(Issue.builder()
                        .type("[WCAG 4.1.2] ARIA Role Empty")
                        .wcag("4.1.2")
                        .message("[WCAG 4.1.2] Element has an empty role attribute which is invalid.")
                        .severity(Severity.MEDIUM)
                        .category(Category.STRUCTURE)
                        .element(truncate(el.outerHtml()))
                        .suggestion("Remove the role attribute or assign a valid WAI-ARIA role.")
                        .build());
            } else if (!VALID_ROLES.contains(role)) {
                issues.add(Issue.builder()
                        .type("[WCAG 4.1.2] ARIA Role Invalid")
                        .wcag("4.1.2")
                        .message("[WCAG 4.1.2] Unknown ARIA role '" + role + "' on <" + el.tagName() + "> element.")
                        .severity(Severity.HIGH)
                        .category(Category.STRUCTURE)
                        .element(truncate(el.outerHtml()))
                        .suggestion("Replace '" + role + "' with a valid WAI-ARIA role from the specification.")
                        .build());
            }
        }

        log.debug("AriaRoleRule found {} issues", issues.size());
        return issues;
    }

    private String truncate(String html) {
        return html.length() > 300 ? html.substring(0, 300) + "..." : html;
    }
}
