package org.garvsurve.accessiblity_analyser.model;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Issue {
    private String type;
    private String message;
    private String severity;
    private String element;
}
