package org.garvsurve.auditable.service;

import org.garvsurve.auditable.dto.ScoreBreakdown;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoreService {

    public int calculateFinalScore(List<Issue> issues) {
        Map<Category, Integer> categoryScores = calculateCategoryScores(issues);
        return categoryScores.values().stream().mapToInt(Integer::intValue).sum();
    }

    public ScoreBreakdown getScoreBreakdown(List<Issue> issues) {
        return ScoreBreakdown.builder()
                .categoryScores(calculateCategoryScores(issues))
                .build();
    }

    private Map<Category, Integer> calculateCategoryScores(List<Issue> issues) {
        Map<Category, Integer> maxScores = new HashMap<>();
        maxScores.put(Category.IMAGES, 25);
        maxScores.put(Category.FORMS, 30);
        maxScores.put(Category.STRUCTURE, 30);
        maxScores.put(Category.LINKS, 15);

        Map<Category, Integer> deductions = new HashMap<>();
        for (Category category : Category.values()) {
            deductions.put(category, 0);
        }

        for (Issue issue : issues) {
            int deduction = 0;
            if (issue.getSeverity() == Severity.HIGH) deduction = 10;
            else if (issue.getSeverity() == Severity.MEDIUM) deduction = 5;
            else if (issue.getSeverity() == Severity.LOW) deduction = 2;

            deductions.put(issue.getCategory(), deductions.getOrDefault(issue.getCategory(), 0) + deduction);
        }

        Map<Category, Integer> finalCategoryScores = new HashMap<>();
        for (Category category : Category.values()) {
            int max = maxScores.get(category);
            int score = max - deductions.get(category);
            finalCategoryScores.put(category, Math.max(0, score)); // max 0
        }

        return finalCategoryScores;
    }
}
