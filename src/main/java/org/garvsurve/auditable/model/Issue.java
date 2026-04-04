package org.garvsurve.auditable.model;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Issue {
    private String type;
    private String message;
    private Severity severity;
    private Category category;
    private String element;
    private String suggestion;
}
