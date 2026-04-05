package org.garvsurve.auditable.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.parser.HtmlParser;
import org.garvsurve.auditable.rules.Rule;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanService {

    private final HtmlParser htmlParser;
    private final List<Rule>  rules;

    public List<Issue> scan(String url) throws Exception {
        log.info("Starting accessibility scan for URL: {}", url);
        long start = System.currentTimeMillis();

        Document doc = htmlParser.parseFromUrl(url);
        List<Issue> issues = runRules(doc);

        log.info("Scan complete for '{}' — {} issue(s) found in {}ms",
                url, issues.size(), System.currentTimeMillis() - start);
        return issues;
    }

    public List<Issue> scanHtml(String html) {
        log.info("Starting accessibility scan for raw HTML input ({} chars)", html.length());
        Document doc = htmlParser.parseFromHtml(html);
        return runRules(doc);
    }

    private List<Issue> runRules(Document doc) {
        List<Issue> allIssues = new ArrayList<>();

        for (Rule rule : rules) {
            String ruleName = rule.getClass().getSimpleName();
            try {
                log.debug("Running rule: {}", ruleName);
                List<Issue> ruleIssues = rule.check(doc);
                log.debug("Rule '{}' produced {} issue(s)", ruleName, ruleIssues.size());
                allIssues.addAll(ruleIssues);
            } catch (Exception e) {
                log.error("Rule '{}' threw an unexpected exception and was skipped: {}",
                        ruleName, e.getMessage(), e);
            }
        }

        return allIssues;
    }
}
