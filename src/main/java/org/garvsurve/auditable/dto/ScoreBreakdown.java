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
    private Map<Category, Integer> categoryScores;
}
