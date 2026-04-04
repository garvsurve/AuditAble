package org.garvsurve.auditable.controller;

import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.dto.ScanRequest;
import org.garvsurve.auditable.dto.ScanResponse;
import org.garvsurve.auditable.service.ScanService;
import org.garvsurve.auditable.service.ScoreService;
import org.garvsurve.auditable.service.AiSuggestionService;
import org.garvsurve.auditable.service.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ScanController {

    private final ScanService scanService;
    private final ScoreService scoreService;
    private final AiSuggestionService aiSuggestionService;
    private final PdfService pdfService;

    @PostMapping
    public ResponseEntity<ScanResponse> scan(@RequestBody ScanRequest request) {
        try {
            log.info("Received scan request for URL: {}", request.getUrl());
            List<Issue> issues = scanService.scan(request.getUrl());
            aiSuggestionService.fillSuggestions(issues);

            int score = scoreService.calculateFinalScore(issues);

            return ResponseEntity.ok(ScanResponse.builder()
                    .url(request.getUrl())
                    .score(score)
                    .totalIssues(issues.size())
                    .issues(issues)
                    .breakdown(scoreService.getScoreBreakdown(issues))
                    .build());
        } catch (Exception e) {
            log.error("Failed to scan URL: {}", request.getUrl(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> getPdfReport(@RequestParam String url) {
        try {
            log.info("Generating PDF report for URL: {}", url);
            List<Issue> issues = scanService.scan(url);
            aiSuggestionService.fillSuggestions(issues);
            int score = scoreService.calculateFinalScore(issues);

            byte[] pdfBytes = pdfService.generatePdfReport(url, score, issues);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "accessibility_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Failed to generate PDF for URL: {}", url, e);
            return ResponseEntity.badRequest().build();
        }
    }
}