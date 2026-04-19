package org.garvsurve.auditable.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.garvsurve.auditable.dto.ScoreBreakdown;
import org.garvsurve.auditable.model.Category;
import org.garvsurve.auditable.model.Issue;
import org.garvsurve.auditable.model.Severity;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Generates a styled, human-readable PDF accessibility report.
 *
 * <p>Report sections:
 * <ol>
 *   <li>Title header with URL and timestamp</li>
 *   <li>Overall score badge</li>
 *   <li>Category score breakdown table</li>
 *   <li>Summary statistics (total / HIGH / MEDIUM / LOW counts)</li>
 *   <li>Paginated list of all issues — HIGH issues are highlighted</li>
 * </ol>
 */
@Slf4j
@Service
public class PdfService {

    // Colour palette
    private static final Color COLOUR_ACCENT       = new Color(99, 102, 241);
    private static final Color COLOUR_HIGH         = new Color(220, 50,  50);
    private static final Color COLOUR_MEDIUM       = new Color(217, 119, 6);
    private static final Color COLOUR_LOW          = new Color(16, 185, 129);
    private static final Color COLOUR_TABLE_HEADER = new Color(55, 55, 75);
    private static final Color COLOUR_ROW_ALT      = new Color(240, 240, 248);
    private static final Color COLOUR_TEXT_DARK    = new Color(20,  20,  20);
    private static final Color COLOUR_WHITE        = Color.WHITE;

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
            .withZone(ZoneId.of("UTC"));

    // -------------------------------------------------------

    public byte[] generatePdfReport(String url,
                                    int score,
                                    List<Issue> issues,
                                    ScoreBreakdown breakdown) throws DocumentException {

        log.info("Generating PDF report for '{}' (score={}, issues={})", url, score, issues.size());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
        PdfWriter.getInstance(doc, out);
        doc.open();

        addTitle(doc, url);
        addScoreBadge(doc, score);
        addBreakdownTable(doc, breakdown);
        addSummaryStats(doc, issues);
        addIssueList(doc, issues);

        doc.close();
        log.info("PDF report generated successfully ({} bytes)", out.size());
        return out.toByteArray();
    }

    // ---- Legacy overload (backward-compat with controller code that passes no breakdown) ----
    public byte[] generatePdfReport(String url, int score, List<Issue> issues) throws DocumentException {
        return generatePdfReport(url, score, issues, null);
    }

    // -------------------------------------------------------
    // Section builders
    // -------------------------------------------------------

    private void addTitle(Document doc, String url) throws DocumentException {
        Font titleFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, COLOUR_ACCENT);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOUR_TEXT_DARK);
        Font labelFont   = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);

        Paragraph title = new Paragraph("Accessibility Analysis Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        doc.add(title);

        Paragraph urlLine = new Paragraph("URL: " + url, subtitleFont);
        urlLine.setAlignment(Element.ALIGN_CENTER);
        urlLine.setSpacingAfter(2);
        doc.add(urlLine);

        Paragraph ts = new Paragraph("Generated: " + TS_FMT.format(Instant.now()), labelFont);
        ts.setAlignment(Element.ALIGN_CENTER);
        ts.setSpacingAfter(20);
        doc.add(ts);

        // Separator line
        doc.add(new Paragraph("_".repeat(80)));
        doc.add(Chunk.NEWLINE);
    }

    private void addScoreBadge(Document doc, int score) throws DocumentException {
        Color badgeColour = score >= 80 ? COLOUR_LOW : (score >= 50 ? COLOUR_MEDIUM : COLOUR_HIGH);

        Font badgeFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 36, badgeColour);
        Font labelFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOUR_TEXT_DARK);

        Paragraph label = new Paragraph("Overall Accessibility Score", labelFont);
        label.setAlignment(Element.ALIGN_CENTER);
        label.setSpacingAfter(4);
        doc.add(label);

        Paragraph badge = new Paragraph(score + " / 100", badgeFont);
        badge.setAlignment(Element.ALIGN_CENTER);
        badge.setSpacingAfter(20);
        doc.add(badge);
    }

    private void addBreakdownTable(Document doc, ScoreBreakdown breakdown) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOUR_TEXT_DARK);
        Paragraph heading = new Paragraph("Category Score Breakdown", sectionFont);
        heading.setSpacingAfter(8);
        doc.add(heading);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 2f});

        addTableHeader(table, "Category", "Normalised Score (0-100)", "Weight");

        Map<Category, Integer> scores  = breakdown != null && breakdown.getCategoryScores()  != null
                ? breakdown.getCategoryScores()  : Map.of();
        Map<Category, Double>  weights = breakdown != null && breakdown.getCategoryWeights() != null
                ? breakdown.getCategoryWeights() : Map.of();

        boolean alt = false;
        for (Category cat : Category.values()) {
            int    s = scores.getOrDefault(cat, 100);
            double w = weights.getOrDefault(cat, 0.0) * 100;
            Color bg = alt ? COLOUR_ROW_ALT : COLOUR_WHITE;
            addTableRow(table, cat.name(), s + " / 100", String.format("%.0f%%", w), bg);
            alt = !alt;
        }

        doc.add(table);
        doc.add(Chunk.NEWLINE);
    }

    private void addSummaryStats(Document doc, List<Issue> issues) throws DocumentException {
        long high   = issues.stream().filter(i -> i.getSeverity() == Severity.HIGH).count();
        long medium = issues.stream().filter(i -> i.getSeverity() == Severity.MEDIUM).count();
        long low    = issues.stream().filter(i -> i.getSeverity() == Severity.LOW).count();

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOUR_TEXT_DARK);
        Font statFont    = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOUR_TEXT_DARK);

        doc.add(new Paragraph("Issue Summary", sectionFont));

        Paragraph stats = new Paragraph();
        stats.add(new Chunk("Total: " + issues.size() + "    ", statFont));
        stats.add(new Chunk("HIGH: " + high,   FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOUR_HIGH)));
        stats.add(new Chunk("    MEDIUM: " + medium, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOUR_MEDIUM)));
        stats.add(new Chunk("    LOW: " + low,  FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOUR_LOW)));
        stats.setSpacingAfter(16);
        doc.add(stats);
    }

    private void addIssueList(Document doc, List<Issue> issues) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOUR_TEXT_DARK);
        doc.add(new Paragraph("Identified Issues", sectionFont));
        doc.add(Chunk.NEWLINE);

        Font numberFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOUR_ACCENT);
        Font typeFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOUR_TEXT_DARK);
        Font bodyFont    = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOUR_TEXT_DARK);
        Font codeFont    = FontFactory.getFont(FontFactory.COURIER, 9, COLOUR_TEXT_DARK);
        Font suggFont    = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, new Color(60, 60, 200));

        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            boolean isHigh = issue.getSeverity() == Severity.HIGH;

            // Highlight HIGH issues with a coloured background cell
            PdfPTable issueTable = new PdfPTable(1);
            issueTable.setWidthPercentage(100);
            issueTable.setSpacingAfter(10);

            PdfPCell cell = new PdfPCell();
            cell.setPadding(8);
            cell.setBackgroundColor(isHigh ? new Color(255, 240, 240) : COLOUR_WHITE);
            cell.setBorderColor(isHigh ? COLOUR_HIGH : new Color(210, 210, 210));
            cell.setBorderWidth(isHigh ? 1.5f : 0.5f);

            Phrase content = new Phrase();
            content.add(new Chunk("#" + (i + 1) + "  ", numberFont));
            content.add(new Chunk(issue.getType(), typeFont));
            content.add(Chunk.NEWLINE);

            Color sevColour = switch (issue.getSeverity()) {
                case HIGH   -> COLOUR_HIGH;
                case MEDIUM -> COLOUR_MEDIUM;
                case LOW    -> COLOUR_LOW;
            };
            Font sevFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, sevColour);
            content.add(new Chunk("  Severity: " + issue.getSeverity()
                    + "  |  Category: " + issue.getCategory() + "\n", sevFont));
            content.add(new Chunk("  " + issue.getMessage() + "\n", bodyFont));

            if (issue.getElement() != null && !issue.getElement().isBlank()) {
                String snippet = issue.getElement().length() > 200
                        ? issue.getElement().substring(0, 200) + "..."
                        : issue.getElement();
                content.add(new Chunk("  HTML: " + snippet + "\n", codeFont));
            }
            if (issue.getSuggestion() != null && !issue.getSuggestion().isBlank()) {
                content.add(new Chunk("  💡 " + issue.getSuggestion() + "\n", suggFont));
            }

            cell.setPhrase(content);
            issueTable.addCell(cell);
            doc.add(issueTable);
        }
    }

    // -------------------------------------------------------
    // Table helpers
    // -------------------------------------------------------

    private void addTableHeader(PdfPTable table, String... headers) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOUR_WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, f));
            cell.setBackgroundColor(COLOUR_TABLE_HEADER);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, String col1, String col2, String col3, Color bg) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOUR_TEXT_DARK);
        for (String v : new String[]{col1, col2, col3}) {
            PdfPCell cell = new PdfPCell(new Phrase(v, f));
            cell.setBackgroundColor(bg);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}
