package org.garvsurve.auditable.service;

import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.dto.ScoreBreakdown;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ScoreService {

    // Weights: IMAGES 25% | FORMS 30% | STRUCTURE 30% | LINKS 15%
    private static final Map<Category, Double> WEIGHTS = Map.of(
            Category.IMAGES,    0.25,
            Category.FORMS,     0.30,
            Category.STRUCTURE, 0.30,
            Category.LINKS,     0.15
    );

    private static final int DEDUCTION_HIGH   = 15;
    private static final int DEDUCTION_MEDIUM = 8;
    private static final int DEDUCTION_LOW    = 3;

    public int calculateFinalScore(List<Issue> issues) {
        return buildBreakdown(issues).getFinalScore();
    }

    public ScoreBreakdown getScoreBreakdown(List<Issue> issues) {
        return buildBreakdown(issues);
    }

    private ScoreBreakdown buildBreakdown(List<Issue> issues) {
        Map<Category, Integer> normalised = normalisedCategoryScores(issues);

        double weightedSum = 0.0;
        Map<Category, Double> weights = new EnumMap<>(Category.class);

        for (Category cat : Category.values()) {
            double w = WEIGHTS.getOrDefault(cat, 0.0);
            weights.put(cat, w);
            weightedSum += normalised.getOrDefault(cat, 100) * w;
        }

        int finalScore = (int) Math.round(weightedSum);
        log.debug("Score breakdown – category scores: {}, finalScore: {}", normalised, finalScore);

        return ScoreBreakdown.builder()
                .categoryScores(normalised)
                .categoryWeights(weights)
                .finalScore(finalScore)
                .build();
    }

    private Map<Category, Integer> normalisedCategoryScores(List<Issue> issues) {
        Map<Category, Integer> deductions = new EnumMap<>(Category.class);
        for (Category c : Category.values()) deductions.put(c, 0);

        for (Issue issue : issues) {
            int deduction = deductionFor(issue.getSeverity());
            deductions.merge(issue.getCategory(), deduction, (a, b) -> a + b);
        }

        Map<Category, Integer> scores = new EnumMap<>(Category.class);
        for (Category c : Category.values()) {
            scores.put(c, Math.max(0, 100 - deductions.get(c)));
        }
        return scores;
    }

    private int deductionFor(Severity severity) {
        if (severity == null) return 0;
        return switch (severity) {
            case HIGH   -> DEDUCTION_HIGH;
            case MEDIUM -> DEDUCTION_MEDIUM;
            case LOW    -> DEDUCTION_LOW;
        };
    }
}
