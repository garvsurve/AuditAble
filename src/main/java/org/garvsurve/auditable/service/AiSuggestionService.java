package org.garvsurve.auditable.service;

import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiSuggestionService {

    private static final int MAX_AI_CALLS = 5;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void fillSuggestions(List<Issue> issues) {
        int aiCallsUsed = 0;

        for (Issue issue : issues) {
            Severity sev = issue.getSeverity();

            if (sev == Severity.LOW) {
                issue.setSuggestion(staticFallback(issue.getType(), issue.getMessage()));
                continue;
            }

            boolean shouldCallAi = (sev == Severity.HIGH) ||
                                   (sev == Severity.MEDIUM && aiCallsUsed < MAX_AI_CALLS);

            if (shouldCallAi && aiCallsUsed < MAX_AI_CALLS) {
                log.info("Requesting AI suggestion for issue type='{}' severity='{}'",
                        issue.getType(), sev);
                issue.setSuggestion(generateSuggestion(issue));
                aiCallsUsed++;
            } else {
                issue.setSuggestion(staticFallback(issue.getType(), issue.getMessage()));
            }
        }

        log.info("AI suggestion pass complete – {} API calls made out of max {}", aiCallsUsed, MAX_AI_CALLS);
    }

    private String generateSuggestion(Issue issue) {
        if (!isApiKeyConfigured()) {
            log.warn("Gemini API key not configured – using static fallback.");
            return staticFallback(issue.getType(), issue.getMessage());
        }

        try {
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                    + geminiApiKey;

            String elementSnippet = issue.getElement() != null
                    ? issue.getElement().substring(0, Math.min(issue.getElement().length(), 300))
                    : "N/A";

            String prompt = String.format(
                    """
                    You are a senior web accessibility expert specialising in WCAG 2.1 compliance.

                    An automated scan found the following accessibility issue:
                    - Issue Type: %s
                    - Severity: %s
                    - Description: %s
                    - Failing HTML snippet: %s

                    Please respond with:
                    1. A concise explanation of why this is an accessibility problem (1 sentence).
                    2. The exact fix required (1–2 sentences).
                    3. A corrected HTML code example that resolves the issue.

                    Keep your entire response under 150 words. Use plain text only (no markdown).
                    """,
                    issue.getType(),
                    issue.getSeverity(),
                    issue.getMessage(),
                    elementSnippet
            );

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "generationConfig", Map.of(
                            "maxOutputTokens", 300,
                            "temperature", 0.2
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("candidates")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    if (content != null && content.containsKey("parts")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (!parts.isEmpty()) {
                            String text = (String) parts.get(0).get("text");
                            if (text != null && !text.isBlank()) {
                                return text.trim();
                            }
                        }
                    }
                }
            }

            log.warn("Gemini returned an unexpected response structure – using static fallback.");
            return staticFallback(issue.getType(), issue.getMessage());

        } catch (Exception e) {
            log.error("Gemini API call failed for issue '{}': {}", issue.getType(), e.getMessage());
            return staticFallback(issue.getType(), issue.getMessage());
        }
    }

    private boolean isApiKeyConfigured() {
        return geminiApiKey != null
                && !geminiApiKey.isBlank()
                && !geminiApiKey.equalsIgnoreCase("your_api_key_here");
    }

    private String staticFallback(String type, String message) {
        if (type == null) return "Review this element for accessibility compliance.";
        String t = type.toLowerCase();

        if (t.contains("alt") || t.contains("image"))
            return "Add a descriptive alt attribute to the <img> element. " +
                   "Example: <img src=\"photo.jpg\" alt=\"A developer coding at a desk\">. " +
                   "Decorative images should use alt=\"\".";

        if (t.contains("label") || t.contains("input"))
            return "Associate a <label> element with the input using a matching 'for' attribute and input 'id', " +
                   "or add an aria-label attribute. Example: <label for=\"email\">Email</label><input id=\"email\" type=\"email\">.";

        if (t.contains("link") || t.contains("anchor"))
            return "Provide meaningful link text that describes the destination. " +
                   "Avoid generic text like 'click here'. Example: <a href=\"/report\">Download accessibility report</a>.";

        if (t.contains("heading") || t.contains("structure"))
            return "Ensure headings follow a logical hierarchy (h1 → h2 → h3). " +
                   "Do not skip heading levels. Every page should have exactly one <h1>.";

        if (t.contains("button"))
            return "Make sure every <button> has descriptive visible text or an aria-label. " +
                   "Example: <button aria-label=\"Close navigation menu\">✕</button>.";

        if (t.contains("aria"))
            return "Use only valid ARIA roles and attributes. " +
                   "Verify roles against the WAI-ARIA specification and ensure required owned elements are present.";

        if (t.contains("duplicate") || t.contains("id"))
            return "Each 'id' must be unique within an HTML document. " +
                   "Duplicate IDs break label associations and ARIA references. Use unique identifiers per element.";

        if (t.contains("landmark"))
            return "Add appropriate landmark elements: <header>, <nav>, <main>, and <footer>. " +
                   "Landmarks help screen-reader users navigate to content quickly.";

        return "Refer to WCAG 2.1 guidelines for resolving this issue: " + message;
    }
}
