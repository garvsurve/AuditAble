package org.garvsurve.auditable.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
public class HtmlParser {

    private static final int TIMEOUT_MS = 12_000;

    public Document parseFromUrl(String rawUrl) throws IOException {
        String normalised = normalise(rawUrl);
        validateUrl(normalised);

        log.info("Fetching HTML from: {}", normalised);

        try {
            return Jsoup.connect(normalised)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                               "AppleWebKit/537.36 (KHTML, like Gecko) " +
                               "Chrome/124.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate")
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .timeout(TIMEOUT_MS)
                    .get();
        } catch (java.net.SocketTimeoutException e) {
            log.error("Timeout while fetching URL: {}", normalised);
            throw new IOException("Request timed out after " + (TIMEOUT_MS / 1000) + "s for URL: " + normalised, e);
        } catch (IOException e) {
            log.error("Failed to fetch URL {}: {}", normalised, e.getMessage());
            throw new IOException("Could not fetch URL: " + normalised + " — " + e.getMessage(), e);
        }
    }

    public Document parseFromHtml(String html) {
        log.info("Parsing raw HTML input ({} chars)", html.length());
        return Jsoup.parse(html);
    }

    private String normalise(String url) {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("URL must not be blank.");
        String trimmed = url.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }

    private void validateUrl(String url) {
        try {
            URI.create(url);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
    }
}
