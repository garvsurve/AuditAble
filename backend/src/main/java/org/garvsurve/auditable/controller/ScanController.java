package org.garvsurve.auditable.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.dto.ScanRequest;
import org.garvsurve.auditable.dto.ScanResponse;
import org.garvsurve.auditable.dto.ScoreBreakdown;
import org.garvsurve.auditable.dto.TestScanRequest;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.service.AiSuggestionService;
import org.garvsurve.auditable.service.PdfService;
import org.garvsurve.auditable.service.ScanService;
import org.garvsurve.auditable.service.ScoreService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScanController {

    private final ScanService         scanService;
    private final ScoreService        scoreService;
    private final AiSuggestionService aiSuggestionService;
    private final PdfService          pdfService;

    @PostMapping
    public ResponseEntity<?> scan(@RequestBody ScanRequest request) {
        String url = request.getUrl();

        if (url == null || url.isBlank()) {
            return badRequest("Invalid URL", "Please provide a valid website URL.");
        }

        long start = System.currentTimeMillis();
        log.info("Received scan request for URL: {}", url);

        try {
            List<Issue> issues = scanService.scan(url);
            aiSuggestionService.fillSuggestions(issues);

            ScoreBreakdown breakdown = scoreService.getScoreBreakdown(issues);
            int score = breakdown.getFinalScore();
            long duration = System.currentTimeMillis() - start;

            ScanResponse response = ScanResponse.builder()
                    .url(url)
                    .score(score)
                    .totalIssues(issues.size())
                    .issues(issues)
                    .breakdown(breakdown)
                    .metadata(ScanResponse.ScanMetadata.builder()
                            .scannedUrl(url)
                            .timestamp(Instant.now().toString())
                            .totalIssues(issues.size())
                            .durationMs(duration)
                            .build())
                    .build();

            log.info("Scan completed for '{}' — score={}, issues={}, duration={}ms",
                    url, score, issues.size(), duration);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid URL supplied: {} — {}", url, e.getMessage());
            return badRequest("Invalid URL", e.getMessage());
        } catch (java.io.IOException e) {
            log.error("Network error while scanning URL {}: {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(errorBody("Network Error",
                            "Could not reach the URL. It may be blocked, offline, or inaccessible: "
                            + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error scanning URL {}: {}", url, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(errorBody("Scan Failed", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<?> getPdfReport(@RequestParam String url) {
        if (url == null || url.isBlank()) {
            return badRequest("Invalid URL", "Please provide a valid website URL.");
        }

        log.info("Generating PDF report for URL: {}", url);

        try {
            List<Issue> issues = scanService.scan(url);
            aiSuggestionService.fillSuggestions(issues);

            ScoreBreakdown breakdown = scoreService.getScoreBreakdown(issues);
            int score = breakdown.getFinalScore();

            byte[] pdfBytes = pdfService.generatePdfReport(url, score, issues, breakdown);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "accessibility_report.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);

        } catch (IllegalArgumentException e) {
            return badRequest("Invalid URL", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to generate PDF for URL {}: {}", url, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(errorBody("PDF Generation Failed", e.getMessage()));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> testScan(@RequestBody TestScanRequest request) {
        if (request.getHtml() == null || request.getHtml().isBlank()) {
            return badRequest("Empty HTML", "Please provide non-empty HTML content.");
        }

        log.info("Test scan requested with {} chars of raw HTML", request.getHtml().length());

        try {
            List<Issue> issues = scanService.scanHtml(request.getHtml());
            ScoreBreakdown breakdown = scoreService.getScoreBreakdown(issues);
            int score = breakdown.getFinalScore();

            ScanResponse response = ScanResponse.builder()
                    .url("raw-html-input")
                    .score(score)
                    .totalIssues(issues.size())
                    .issues(issues)
                    .breakdown(breakdown)
                    .metadata(ScanResponse.ScanMetadata.builder()
                            .scannedUrl("raw-html-input")
                            .timestamp(Instant.now().toString())
                            .totalIssues(issues.size())
                            .durationMs(0L)
                            .build())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Test scan failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(errorBody("Test Scan Failed", e.getMessage()));
        }
    }

    private ResponseEntity<Map<String, String>> badRequest(String error, String message) {
        return ResponseEntity.badRequest().body(errorBody(error, message));
    }

    private Map<String, String> errorBody(String error, String message) {
        return Map.of("error", error, "message", message);
    }
}