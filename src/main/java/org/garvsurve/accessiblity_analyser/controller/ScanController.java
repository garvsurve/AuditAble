package org.garvsurve.accessiblity_analyser.controller;

import org.garvsurve.accessiblity_analyser.model.Issue;
import org.garvsurve.accessiblity_analyser.dto.ScanRequest;
import org.garvsurve.accessiblity_analyser.dto.ScanResponse;
import org.garvsurve.accessiblity_analyser.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @PostMapping
    public ScanResponse scan(@RequestBody ScanRequest request) throws Exception {

        List<Issue> issues = scanService.scan(request.getUrl());

        return ScanResponse.builder()
                .url(request.getUrl())
                .totalIssues(issues.size())
                .issues(issues)
                .build();
    }
}