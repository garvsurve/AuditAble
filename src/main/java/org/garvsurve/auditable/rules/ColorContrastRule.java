package org.garvsurve.auditable.rules;

import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ColorContrastRule — WCAG 1.4.3 (Contrast Minimum)
 *
 * Detects elements that have both an inline {@code color} and
 * {@code background-color} style where a simple heuristic suggests the
 * combination may produce insufficient contrast (e.g. light-on-light or
 * dark-on-dark pairings).
 *
 * <p>Note: A full WCAG contrast-ratio calculation requires converting colours
 * to relative luminance (L) and computing (L1+0.05)/(L2+0.05). That involves
 * floating-point gamma correction and is deliberately out of scope here.
 * Instead we use a lightweight brightness approximation to flag the most
 * obvious violations without pulling in any external library.
 */
@Slf4j
@Component
public class ColorContrastRule implements Rule {

    /**
     * Named CSS colours mapped to their approximate 0–255 brightness value.
     * Expand this map as needed; unknown colours are skipped rather than
     * generating false positives.
     */
    private static final Map<String, Integer> NAMED_BRIGHTNESS = new HashMap<>();

    static {
        // Very light colours (brightness > 200)
        NAMED_BRIGHTNESS.put("white",        255);
        NAMED_BRIGHTNESS.put("whitesmoke",   245);
        NAMED_BRIGHTNESS.put("snow",         244);
        NAMED_BRIGHTNESS.put("floralwhite",  242);
        NAMED_BRIGHTNESS.put("ivory",        240);
        NAMED_BRIGHTNESS.put("lightyellow",  237);
        NAMED_BRIGHTNESS.put("lightcyan",    232);
        NAMED_BRIGHTNESS.put("aliceblue",    232);
        NAMED_BRIGHTNESS.put("lavender",     230);
        NAMED_BRIGHTNESS.put("seashell",     229);
        NAMED_BRIGHTNESS.put("ghostwhite",   228);
        NAMED_BRIGHTNESS.put("honeydew",     227);
        NAMED_BRIGHTNESS.put("mintcream",    226);
        NAMED_BRIGHTNESS.put("azure",        225);
        NAMED_BRIGHTNESS.put("lightgray",    211);
        NAMED_BRIGHTNESS.put("lightgrey",    211);
        NAMED_BRIGHTNESS.put("silver",       192);
        // Mid-range colours (roughly 100–200)
        NAMED_BRIGHTNESS.put("gray",         128);
        NAMED_BRIGHTNESS.put("grey",         128);
        NAMED_BRIGHTNESS.put("darkgray",     169);
        NAMED_BRIGHTNESS.put("darkgrey",     169);
        NAMED_BRIGHTNESS.put("dimgray",       99);
        NAMED_BRIGHTNESS.put("dimgrey",       99);
        NAMED_BRIGHTNESS.put("slategray",    112);
        NAMED_BRIGHTNESS.put("slategrey",    112);
        // Very dark colours (brightness < 80)
        NAMED_BRIGHTNESS.put("black",          0);
        NAMED_BRIGHTNESS.put("darkslategray",  47);
        NAMED_BRIGHTNESS.put("darkslategrey",  47);
        NAMED_BRIGHTNESS.put("midnightblue",   26);
        NAMED_BRIGHTNESS.put("navy",           26);
        NAMED_BRIGHTNESS.put("darkgreen",      50);
        NAMED_BRIGHTNESS.put("maroon",         55);
        NAMED_BRIGHTNESS.put("darkred",        55);
        NAMED_BRIGHTNESS.put("indigo",         43);
    }

    /** Minimum perceived-brightness difference considered acceptable. */
    private static final int MIN_BRIGHTNESS_DIFF = 125;

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        // Select every element that carries an inline style attribute
        for (Element el : doc.select("[style]")) {
            String style = el.attr("style");

            String fgColor = extractStyleValue(style, "color");
            String bgColor = extractStyleValue(style, "background-color");

            // We can only analyse the pair when both values are present inline
            if (fgColor == null || bgColor == null) {
                continue;
            }

            Integer fgBrightness = parseBrightness(fgColor);
            Integer bgBrightness = parseBrightness(bgColor);

            // Skip unknown colour formats rather than produce false positives
            if (fgBrightness == null || bgBrightness == null) {
                continue;
            }

            int diff = Math.abs(fgBrightness - bgBrightness);

            if (diff < MIN_BRIGHTNESS_DIFF) {
                issues.add(Issue.builder()
                        .type("[WCAG 1.4.3] Insufficient Colour Contrast")
                        .wcag("1.4.3")
                        .message(String.format(
                                "[WCAG 1.4.3] Low contrast detected: color=\"%s\" on background-color=\"%s\" " +
                                "(brightness difference: %d, minimum required: %d).",
                                fgColor, bgColor, diff, MIN_BRIGHTNESS_DIFF))
                        .severity(Severity.HIGH)
                        .category(Category.STRUCTURE)
                        .element(truncate(el.outerHtml()))
                        .suggestion("Increase the contrast ratio to at least 4.5:1 for normal text (3:1 for large " +
                                    "text). Use a tool such as the WebAIM Contrast Checker to verify the exact ratio " +
                                    "before shipping.")
                        .build());
            }
        }

        log.debug("ColorContrastRule found {} issues", issues.size());
        return issues;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Extracts the value for a given CSS property from an inline style string.
     * Handles optional whitespace around the colon and semicolons.
     *
     * @param style    The raw {@code style} attribute value.
     * @param property The CSS property name (e.g. "color").
     * @return The trimmed property value, or {@code null} if not present.
     */
    private String extractStyleValue(String style, String property) {
        for (String declaration : style.split(";")) {
            String[] parts = declaration.split(":", 2);
            if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(property)) {
                return parts[1].trim().toLowerCase();
            }
        }
        return null;
    }

    /**
     * Converts a colour string to an approximate perceptual brightness (0–255).
     * Supports:
     * <ul>
     *   <li>Named colours present in {@link #NAMED_BRIGHTNESS}</li>
     *   <li>6-digit hex colours {@code #rrggbb}</li>
     *   <li>3-digit hex colours {@code #rgb}</li>
     *   <li>{@code rgb(r, g, b)} functional notation</li>
     * </ul>
     *
     * @param colour The colour string (already lower-cased).
     * @return The brightness value, or {@code null} when the format is unknown.
     */
    private Integer parseBrightness(String colour) {
        // 1. Named colour lookup
        if (NAMED_BRIGHTNESS.containsKey(colour)) {
            return NAMED_BRIGHTNESS.get(colour);
        }

        // 2. Hex colour — #rrggbb or #rgb
        if (colour.startsWith("#")) {
            try {
                int r, g, b;
                if (colour.length() == 7) {
                    r = Integer.parseInt(colour.substring(1, 3), 16);
                    g = Integer.parseInt(colour.substring(3, 5), 16);
                    b = Integer.parseInt(colour.substring(5, 7), 16);
                } else if (colour.length() == 4) {
                    // Expand shorthand: #rgb → #rrggbb
                    r = Integer.parseInt(colour.substring(1, 2) + colour.charAt(1), 16);
                    g = Integer.parseInt(colour.substring(2, 3) + colour.charAt(2), 16);
                    b = Integer.parseInt(colour.substring(3, 4) + colour.charAt(3), 16);
                } else {
                    return null;
                }
                return perceivedBrightness(r, g, b);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // 3. rgb(r, g, b) notation
        if (colour.startsWith("rgb(") && colour.endsWith(")")) {
            String inner = colour.substring(4, colour.length() - 1);
            String[] parts = inner.split(",");
            if (parts.length == 3) {
                try {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    return perceivedBrightness(r, g, b);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        return null; // Unknown format — skip
    }

    /**
     * Calculates perceived brightness using the standard luminance formula
     * (ITU-R BT.601): {@code 0.299R + 0.587G + 0.114B}.
     */
    private int perceivedBrightness(int r, int g, int b) {
        return (int) Math.round(0.299 * r + 0.587 * g + 0.114 * b);
    }

    private String truncate(String html) {
        return html.length() > 300 ? html.substring(0, 300) + "..." : html;
    }
}
