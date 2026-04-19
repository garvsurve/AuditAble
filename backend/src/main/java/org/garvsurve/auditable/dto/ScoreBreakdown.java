package org.garvsurve.auditable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.garvsurve.auditable.model.Category;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoreBreakdown {
    // Per-category normalised scores (0–100 each)
    private Map<Category, Integer> categoryScores;
    // Weighted contribution of each category to the final score
    private Map<Category, Double> categoryWeights;
    // Final weighted score (0–100)
    private int finalScore;
}
