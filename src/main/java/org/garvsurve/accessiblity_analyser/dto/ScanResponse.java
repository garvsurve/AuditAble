package org.garvsurve.accessiblity_analyser.dto;


import lombok.Builder;
import lombok.Data;
import org.garvsurve.accessiblity_analyser.model.Issue;

import java.util.List;

@Data
@Builder
public class ScanResponse {
    private String url;
    private int totalIssues;
    private List<Issue> issues;
}
