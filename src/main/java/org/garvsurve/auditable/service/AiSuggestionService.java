package org.garvsurve.auditable.service;

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

@Service
public class AiSuggestionService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void fillSuggestions(List<Issue> issues) {
        int count = 0;
        for (Issue issue : issues) {
            if (issue.getSeverity() == Severity.HIGH && count < 5) {
                String suggestion = generateSuggestion(issue.getType(), issue.getElement());
                issue.setSuggestion(suggestion);
                count++;
            }
        }
    }

    private String generateSuggestion(String type, String element) {
        if (geminiApiKey == null || geminiApiKey.isEmpty() || geminiApiKey.contains("your_api_key_here")) {
            return "AI Suggestion (Mock): For '" + type + "', check ARIA labels. Element: " + element.substring(0, Math.min(element.length(), 20));
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;
            
            String prompt = "You are an expert accessibility auditor. Give a short, concise, 1-2 sentence plain-text suggestion on how to fix the following accessibility issue: Type: " + type + ". HTML Element snippet: " + element;

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    if (content.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (!parts.isEmpty()) {
                            String text = (String) parts.get(0).get("text");
                            return text.trim();
                        }
                    }
                }
            }
            return "AI Suggestion: Could not generate suggestion.";
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return "AI Suggestion: Error communicating with AI service.";
        }
    }
}
