package org.garvsurve.auditable.rules;
import org.garvsurve.auditable.model.Issue;

import org.jsoup.nodes.Document;

import java.util.List;

public interface Rule {
    List<Issue> check(Document doc);
}
