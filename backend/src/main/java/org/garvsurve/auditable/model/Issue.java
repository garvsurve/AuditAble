package org.garvsurve.auditable.model;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Issue {
    private String rule;          // e.g. "ColorContrastRule"
    private String type;
    private String wcag;          // e.g. "1.4.3" or "1.3.1, 3.3.2"
    private String message;
    private Severity severity;
    private Category category;
    private String element;
    private String suggestion;
}
