package org.garvsurve.auditable.service;
import org.garvsurve.auditable.model.Issue;

import org.garvsurve.auditable.parser.HtmlParser;
import org.garvsurve.auditable.rules.Rule;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final HtmlParser parser;
    private final List<Rule> rules;

    public List<Issue> scan(String url) throws Exception {
        Document doc = parser.parseFromUrl(url);

        List<Issue> allIssues = new ArrayList<>();

        for (Rule rule : rules) {
            allIssues.addAll(rule.check(doc));
        }

        return allIssues;

    }
}
