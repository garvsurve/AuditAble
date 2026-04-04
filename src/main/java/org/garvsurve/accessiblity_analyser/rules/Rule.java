package org.garvsurve.accessiblity_analyser.rules;
import org.garvsurve.accessiblity_analyser.model.Issue;

import org.jsoup.nodes.Document;

import java.util.List;

public interface Rule {
    List<Issue> check(Document doc);
}
