package org.garvsurve.auditable.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.garvsurve.auditable.model.Issue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generatePdfReport(String url, int score, List<Issue> issues) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        document.add(new Paragraph("Accessibility Analysis Report", titleFont));
        document.add(new Paragraph("URL: " + url));
        document.add(new Paragraph("Score: " + score + " / 100"));
        document.add(new Paragraph("Total Issues: " + issues.size()));
        
        document.add(new Paragraph("\n"));
        
        Font issueTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        
        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            document.add(new Paragraph("Issue #" + (i + 1) + ": " + issue.getType(), issueTitleFont));
            document.add(new Paragraph("Severity: " + issue.getSeverity() + " | Category: " + issue.getCategory()));
            document.add(new Paragraph("Message: " + issue.getMessage()));
            if (issue.getSuggestion() != null) {
                document.add(new Paragraph("AI Suggestion: " + issue.getSuggestion()));
            }
            document.add(new Paragraph("\n"));
        }

        document.close();
        return out.toByteArray();
    }
}
