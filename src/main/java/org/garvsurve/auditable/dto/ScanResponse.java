package org.garvsurve.auditable.dto;

import lombok.Builder;
import lombok.Data;
import org.garvsurve.auditable.model.Issue;

import java.util.List;

@Data
@Builder
public class ScanResponse {
    private String url;
    private int score;
    private int totalIssues;
    private List<Issue> issues;
    private ScoreBreakdown breakdown;
    private ScanMetadata metadata;

    @Data
    @Builder
    public static class ScanMetadata {
        private String scannedUrl;
        private String timestamp;
        private int totalIssues;
        private long durationMs;
    }
}
