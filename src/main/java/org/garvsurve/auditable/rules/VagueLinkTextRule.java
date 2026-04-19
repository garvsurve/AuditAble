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
import java.util.Set;

/**
 * VagueLinkTextRule — WCAG 2.4.4 (Link Purpose – In Context)
 *
 * Detects anchor elements whose accessible text is generic and fails to
 * communicate the link's destination or purpose when read in isolation by a
 * screen reader (e.g. "click here", "read more", "here", "link").
 *
 * <p>The existing {@link LinkTextRule} already flags <em>empty</em> links. This
 * rule complements it by catching links that <em>have</em> text but whose text
 * conveys no meaningful information.
 *
 * <p>Checks performed:
 * <ol>
 *   <li>The anchor's visible text (trimmed, lower-cased) is compared against a
 *       predefined set of vague phrases.</li>
 *   <li>If the element has a non-empty {@code aria-label} that is <em>not</em>
 *       itself vague, the failure is suppressed — the author has already
 *       provided a programmatic alternative.</li>
 * </ol>
 */
@Slf4j
@Component
public class VagueLinkTextRule implements Rule {

    /**
     * Lower-cased vague link-text phrases that should be flagged.
     * Comparison is done after stripping leading/trailing whitespace.
     */
    private static final Set<String> VAGUE_PHRASES = Set.of(
            "click here",
            "click",
            "here",
            "read more",
            "more",
            "link",
            "this link",
            "learn more",
            "go",
            "continue",
            "details",
            "info",
            "information",
            "see more",
            "view more",
            "more info",
            "more information",
            "download",
            "open"
    );

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element link : doc.select("a[href]")) {
            String visibleText = link.text().trim().toLowerCase();

            // Empty links are handled by LinkTextRule — skip here
            if (visibleText.isEmpty()) {
                continue;
            }

            if (!VAGUE_PHRASES.contains(visibleText)) {
                continue; // Visible text appears descriptive — no issue
            }

            // Check whether a non-vague aria-label rescues the element
            String ariaLabel = link.attr("aria-label").trim().toLowerCase();
            if (!ariaLabel.isEmpty() && !VAGUE_PHRASES.contains(ariaLabel)) {
                continue; // aria-label provides meaningful context
            }

            issues.add(Issue.builder()
                    .type("[WCAG 2.4.4] Vague Link Text")
                    .wcag("2.4.4")
                    .message(String.format(
                            "[WCAG 2.4.4] Link text \"%s\" does not describe the link's purpose. " +
                            "Screen reader users navigating by links will have no context for this anchor.",
                            link.text().trim()))
                    .severity(Severity.MEDIUM)
                    .category(Category.LINKS)
                    .element(truncate(link.outerHtml()))
                    .suggestion("Replace vague text with a description of the destination or action, e.g. " +
                                "\"Read the full accessibility guide\" instead of \"read more\". " +
                                "If the surrounding context provides meaning, add an aria-label that " +
                                "reflects that context directly on the <a> element.")
                    .build());
        }

        log.debug("VagueLinkTextRule found {} issues", issues.size());
        return issues;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String truncate(String html) {
        return html.length() > 300 ? html.substring(0, 300) + "..." : html;
    }
}
