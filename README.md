<div align="center">
  
# 🌐 AuditAble
  
[![Java Support](https://img.shields.io/badge/Java-21-blue.svg?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![JSoup](https://img.shields.io/badge/JSoup-1.21.2-orange.svg)]()
[![OpenPDF](https://img.shields.io/badge/OpenPDF-1.3.32-red.svg)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)]()

*A robust, headless API designed to actively crawl a given URL, analyze its HTML DOM, grade it against strict accessibility metrics, and dynamically generate actionable AI-driven UX fixes.*

</div>

---

## 📖 Overview

The **AuditAble** is a Spring Boot application built to emulate a standard web-crawler. It downloads the live Document Object Model (DOM) of any secure website and pushes the elements into a robust, decoupled **Rule Engine** running standard Web Content Accessibility Guidelines (WCAG) checks.

Depending on the HTML structure, the application calculates an intelligent score representing the accessibility health of the site and offers a RESTful JSON evaluation or a visually compiled PDF report.

---

## 🔄 How It Works (The Process)

AuditAble operates in four sequential steps to analyze and grade web pages:

1. 🌐 **Fetching the HTML (Parser):** The `HtmlParser` class takes your target URL and uses **JSoup** to securely connect to the site, bypassing basic protections, and downloading the raw HTML Document Object Model (DOM).
2. 🕵️ **Checking the Rules (Scan Service):** The raw HTML is passed through a list of **Rule Checkers**. Currently, 6 specific rules (e.g., ImageAltRule, ButtonTextRule) scan the DOM for common Web Content Accessibility Guidelines (WCAG) violations, flagging each issue with a severity level (`HIGH`, `MEDIUM`, `LOW`) and capturing the failing HTML snippet.
3. 🥇 **Grading (Score Service):** The engine starts your page at **100 points**. For every rule violation found, it subtracts points dynamically based on the frequency and severity of that specific flaw to give an accurate health representation.
4. 🤖 **Getting AI Fixes (AI Suggestion Service):** The system gathers the top 5 `HIGH` severity issues and securely queries the **Google Gemini AI API**. Gemini analyzes the broken HTML syntax and returns a concise explanation advising developers exactly on how to rectify it.

---

## ✨ Key Features

- 🕵️ **Dynamic Browser Spoofing:** Seamlessly accesses domains protected by base-level Cloudflare or bot-blockers using JSoup User-Agent header injection.
- 📐 **Extensible Rule Engine:** Highly modular architecture allowing new accessibility checks (Rules) to be injected simply via `@Component`.
- 📊 **Smart Grading System:** Granular 100-point ceiling with distinct thresholds spread across logical domains (Images, Links, Forms, Structure).
- 🧠 **AI Suggestion Hooks:** Auto-appends syntactical code-fixes for critical flaws found directly inside the tested DOM element.
- 📄 **PDF Reporting Engine:** Provides high-level stakeholders with a clear, readable `.pdf` output detailing all findings and category breakdowns via OpenPDF.

---

## 🛠️ Technology Stack

| Domain | Technology Let |
| --- | --- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.0.5 |
| **Parsing** | JSoup 1.21.2 |
| **Rendering** | LibrePDF / OpenPDF |
| **Utilities** | Lombok, Maven |

---

## ⚖️ Implementation Rules

The engine currently validates the parsed HTML against the following rules:

1. 🖼️ **`ImageAltRule`** — Ensures all `<img/>` elements carry a descriptive, formatted `alt` tag.
2. 📝 **`InputLabelRule`** — Maps every `<input>` wrapper natively to an associated `<label>`, `aria-label`, or native `aria-labelledby` property.
3. 🔘 **`ButtonTextRule`** — Secures actionable `<button>` tags so they strictly contain readable anchor text or contextually distinct logic.
4. 🏷️ **`TitleRule`** — Scans the global document `<head>` to maintain a descriptive `<title>` for Screen Readers.
5. 📚 **`HeadingStructureRule`** — Blocks heading jumps (e.g., An `<h3>` directly succeeding an `<h1>`).
6. 🔗 **`LinkTextRule`** — Verifies `<a>` tags hold perceivable text blocks or contextual image bindings.

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed on your local machine:
- **[JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)**
- **[Apache Maven](https://maven.apache.org/download.cgi)**

### Build & Run

Clone the repository and spin up the embedded Tomcat server via Maven. Let Spring Boot handle the rest.

```bash
mvn clean package
mvn spring-boot:run
```

By default, the REST server will bind to port `7070` as defined in `application.properties`.

---

## 📡 API Reference

### 1. Execute Engine Scan (JSON)
Executes a head-to-toe DOM evaluation and outputs a structured vulnerability JSON.

**`POST /api/scan`**
```json
// Request Body
{
    "url": "https://www.w3.org/WAI/demos/bad/before/home.html"
}
```

```json
// Example Response
{
  "url": "https://www.w3.org/WAI/demos/bad/before/home.html",
  "score": 67,
  "totalIssues": 34,
  "issues": [
    {
      "type": "Image Alt Missing",
      "message": "Image missing alt attribute",
      "severity": "HIGH",
      "category": "IMAGES",
      "element": "<img src=\"city.png\">",
      "suggestion": "AI Suggestion: For 'Image Alt Missing', ensure you add the appropriate ARIA labels..."
    }
  ],
  "breakdown": {
    "categoryScores": {
      "IMAGES": 5,
      "STRUCTURE": 12,
      "LINKS": 15,
      "FORMS": 30
    }
  }
}
```

---

### 2. Export Diagnostic PDF
Compiles the exact dataset outputted above into a fully formatted binary PDF blob for download. Best used straight in the browser.

**`GET /api/scan/report/pdf?url=<TARGET_URL>`**

**Example Usage In Browser:**
```text
http://localhost:7070/api/scan/report/pdf?url=https://www.w3.org/WAI/demos/bad/before/home.html
```

---

<div align="center">
<i>Built with ❤️ using Java & Spring Boot</i>
</div>
