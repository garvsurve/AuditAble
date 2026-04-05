<div align="center">

<br/>

# 🌐 AuditAble

### Production-grade Web Accessibility Analyzer

<br/>

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-6-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev/)
[![Gemini AI](https://img.shields.io/badge/Gemini_2.5_Flash-AI-8E44AD?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

*Point it at any URL — it crawls the live DOM, scores it against WCAG rules, surfaces AI-powered fix suggestions, and exports a colour-coded PDF report — all in one API call.*

<br/>

</div>

---

## 📸 Preview

> Score gauge · Category breakdown · AI-powered issue cards · PDF export

The React frontend presents a live accessibility score, per-category breakdown bars (with mathematically verified weighted scores), and expandable issue cards with AI fix suggestions — all returned in a single API response.

---

## 🧠 How It Works

```
URL Input
    │
    ▼
┌─────────────┐     ┌──────────────────────────────────────────┐
│  HtmlParser │────▶│              Rule Engine                 │
│  (JSoup)    │     │  ImageAltRule · InputLabelRule           │
└─────────────┘     │  ButtonTextRule · TitleRule              │
                    │  HeadingStructureRule · LinkTextRule     │
                    │  AriaRoleRule · DuplicateIdRule          │
                    │  FormInputAccessibilityRule · LandmarkRule│
                    └────────────────┬─────────────────────────┘
                                     │ List<Issue>
                    ┌────────────────▼─────────────────────────┐
                    │            ScoreService                   │
                    │  Normalise each category → 0-100         │
                    │  Apply weights → weighted final score     │
                    └────────────────┬─────────────────────────┘
                                     │ ScoreBreakdown
                    ┌────────────────▼─────────────────────────┐
                    │         AiSuggestionService               │
                    │  HIGH → always call Gemini               │
                    │  MEDIUM → call if budget (5) remains     │
                    │  LOW → rich static fallback              │
                    └────────────────┬─────────────────────────┘
                                     │
                    ┌────────────────▼─────────────────────────┐
                    │        ScanResponse (JSON)                │
                    │  + PDF Report (optional download)         │
                    └──────────────────────────────────────────┘
```

---

## ✨ Features

| Feature | Details |
|---|---|
| 🛡️ **10 Accessibility Rules** | Images, forms, links, headings, buttons, ARIA roles, duplicate IDs, form input accessibility, landmark structure |
| 📊 **Weighted Scoring** | Each category normalised 0–100, then combined by configurable weights — score always stays in range |
| 🤖 **Smart AI Budget** | HIGH always gets Gemini suggestions · MEDIUM shares a 5-call budget · LOW gets type-aware static fallbacks |
| ⚠️ **Structured Errors** | Every failure returns `{"error": "...", "message": "..."}` with an appropriate HTTP status |
| 🧪 **Raw HTML Test Endpoint** | `POST /api/scan/test` — run rules on raw HTML without a live URL, ideal for CI/CD |
| 📄 **Rich PDF Reports** | Colour-coded score badge · breakdown table · HIGH-severity highlighting · AI suggestion per issue |
| 🌐 **React Frontend** | Animated score gauge · live category breakdown bars · searchable / filterable issue list |
| 📋 **Scan Metadata** | Every response includes timestamp, URL, total issues, and duration |
| 🔒 **Anti-bot Bypass** | JSoup sends browser-spoofed headers to access protected pages |
| 📐 **Plug-in Rules** | Drop a `@Component` class implementing `Rule` — Spring auto-registers it |

---

## 📊 Scoring Formula

**Stage 1 — Normalised per-category score (0–100):**
```
category_score = max(0, 100 − Σ deductions)

Per issue:  HIGH → −15 pts  |  MEDIUM → −8 pts  |  LOW → −3 pts
```

**Stage 2 — Weighted final score:**
```
final_score = round(
    IMAGES    × 0.25  +
    FORMS     × 0.30  +
    STRUCTURE × 0.30  +
    LINKS     × 0.15
)
```

**Live example (from screenshot):**
```
Structure 97% × 30% = 29.1
Images   100% × 25% = 25.0
Links    100% × 15% = 15.0
Forms     10% × 30% =  3.0
─────────────────────────────
Final Score          = 72  ✅
```

---

## ⚖️ Accessibility Rules

### 🖼️ IMAGES — 25% weight
| Rule | Severity | What it checks |
|---|---|---|
| `ImageAltRule` | HIGH | `<img>` missing or empty `alt` attribute |

### 📝 FORMS — 30% weight
| Rule | Severity | What it checks |
|---|---|---|
| `InputLabelRule` | HIGH | Input missing `<label>`, `aria-label`, or `aria-labelledby` |
| `ButtonTextRule` | MEDIUM | `<button>` with no readable text or `aria-label` |
| `FormInputAccessibilityRule` | HIGH | Input missing ALL of: `aria-label`, `placeholder`, and any label |

### 🏗️ STRUCTURE — 30% weight
| Rule | Severity | What it checks |
|---|---|---|
| `TitleRule` | HIGH | Missing or empty `<title>` in document `<head>` |
| `HeadingStructureRule` | MEDIUM | Heading level jumps (e.g. `<h1>` → `<h3>`) |
| `AriaRoleRule` | HIGH/MEDIUM | Invalid or empty `role` attribute (WAI-ARIA 1.2) |
| `DuplicateIdRule` | HIGH | Duplicate `id` values that break label/ARIA associations |
| `LandmarkRule` | HIGH/MEDIUM/LOW | Missing `<header>`, `<nav>`, `<main>`, `<footer>` |

### 🔗 LINKS — 15% weight
| Rule | Severity | What it checks |
|---|---|---|
| `LinkTextRule` | MEDIUM | `<a>` with empty or generic text ("click here", "read more") |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Backend Framework** | Spring Boot 4.0.5 (Spring MVC) |
| **HTML Parsing** | JSoup 1.21.2 |
| **AI** | Google Gemini 2.5 Flash |
| **PDF Generation** | LibrePDF / OpenPDF 1.3.32 |
| **Utilities** | Lombok, SLF4J, Maven |
| **Frontend** | React 19 + Vite 6 |
| **UI Animations** | Framer Motion |
| **Icons** | Lucide React |

---

## 🚀 Getting Started

### Prerequisites

- **[JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)**
- **[Apache Maven 3.x](https://maven.apache.org/download.cgi)**
- **[Node.js 18+](https://nodejs.org/)**
- **[Google Gemini API Key](https://aistudio.google.com/app/apikey)** *(free — optional, static fallbacks work without it)*

### 1. Clone the Repository
```bash
git clone https://github.com/garvsurve/AuditAble.git
cd AuditAble
```

### 2. Configure API Key *(Optional)*

Create `src/main/resources/application-secrets.properties`:
```properties
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```
> Without a key, all suggestions fall back to detailed pre-written guidance.

### 3. Start the Backend
```bash
mvn compile
mvn spring-boot:run
```
The REST API starts on **`http://localhost:7070`**

### 4. Start the Frontend
```bash
cd frontend
npm install
npm run dev
```
The React app starts on **`http://localhost:5173`**

---

## 📡 API Reference

### `POST /api/scan` — Scan a URL

```bash
curl -X POST http://localhost:7070/api/scan \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com"}'
```

**Response:**
```json
{
  "url": "https://example.com",
  "score": 72,
  "totalIssues": 7,
  "issues": [
    {
      "type": "Form Input Inaccessible",
      "message": "Input element of type 'file' has no aria-label, placeholder, or associated <label>.",
      "severity": "HIGH",
      "category": "FORMS",
      "element": "<input type=\"file\">",
      "suggestion": "Associate a <label> element with the input using a matching 'for' attribute..."
    }
  ],
  "breakdown": {
    "categoryScores": {
      "STRUCTURE": 97,
      "IMAGES": 100,
      "LINKS": 100,
      "FORMS": 10
    },
    "categoryWeights": {
      "STRUCTURE": 0.30,
      "IMAGES": 0.25,
      "LINKS": 0.15,
      "FORMS": 0.30
    },
    "finalScore": 72
  },
  "metadata": {
    "scannedUrl": "https://example.com",
    "timestamp": "2026-04-05T04:30:00Z",
    "totalIssues": 7,
    "durationMs": 1850
  }
}
```

**Error Responses:**
```json
// 400 — Invalid or missing URL
{ "error": "Invalid URL", "message": "Invalid URL format: not-a-url" }

// 502 — Site unreachable, blocked, or timed out
{ "error": "Network Error", "message": "Could not reach the URL. It may be blocked or offline." }

// 500 — Unexpected server error
{ "error": "Scan Failed", "message": "An unexpected error occurred." }
```

---

### `GET /api/scan/report/pdf?url=<URL>` — Download PDF Report

```bash
curl "http://localhost:7070/api/scan/report/pdf?url=https://example.com" \
  --output report.pdf
```

Opens/downloads a styled PDF containing:
- **Score badge** (green ≥ 80 · amber ≥ 50 · red < 50)
- **Category breakdown table** with weights
- **Issue summary** (Total · HIGH · MEDIUM · LOW counts)
- **Per-issue cards** with severity highlighting, HTML snippet, and AI suggestion

---

### `POST /api/scan/test` — Test with Raw HTML

Useful for testing specific markup without a live URL, or for CI/CD integration.

```bash
curl -X POST http://localhost:7070/api/scan/test \
  -H "Content-Type: application/json" \
  -d '{"html": "<html><body><img src=\"photo.jpg\"><form><input type=\"text\"></form></body></html>"}'
```

Returns the same response schema as `POST /api/scan` with `url: "raw-html-input"`.

---

## 📁 Project Structure

```
AuditAble/
├── src/main/java/org/garvsurve/auditable/
│   ├── AuditAbleApplication.java
│   ├── controller/
│   │   └── ScanController.java              # REST endpoints (scan, PDF, test)
│   ├── dto/
│   │   ├── ScanRequest.java                 # { url }
│   │   ├── ScanResponse.java                # Full response + ScanMetadata inner class
│   │   ├── ScoreBreakdown.java              # categoryScores · categoryWeights · finalScore
│   │   └── TestScanRequest.java             # { html }
│   ├── model/
│   │   ├── Category.java                    # IMAGES · FORMS · STRUCTURE · LINKS
│   │   ├── Issue.java                       # type · message · severity · category · element · suggestion
│   │   └── Severity.java                    # HIGH · MEDIUM · LOW
│   ├── parser/
│   │   └── HtmlParser.java                  # JSoup URL fetcher + raw HTML parser
│   ├── rules/
│   │   ├── Rule.java                        # Interface: List<Issue> check(Document doc)
│   │   ├── ImageAltRule.java
│   │   ├── InputLabelRule.java
│   │   ├── ButtonTextRule.java
│   │   ├── TitleRule.java
│   │   ├── HeadingStructureRule.java
│   │   ├── LinkTextRule.java
│   │   ├── AriaRoleRule.java                ★ NEW
│   │   ├── DuplicateIdRule.java             ★ NEW
│   │   ├── FormInputAccessibilityRule.java  ★ NEW
│   │   └── LandmarkRule.java                ★ NEW
│   └── service/
│       ├── ScanService.java                 # Rule orchestration (per-rule error isolation)
│       ├── ScoreService.java                # Weighted normalised scoring
│       ├── AiSuggestionService.java         # Gemini API + static fallbacks
│       └── PdfService.java                  # Styled PDF generation
├── src/main/resources/
│   ├── application.properties               # Port 7070, secrets import
│   └── application-secrets.properties       # gemini.api.key (git-ignored)
└── frontend/
    └── src/
        ├── pages/
        │   ├── Home.jsx                     # URL input + feature cards
        │   └── Report.jsx                   # Full scan report page
        ├── components/
        │   ├── UrlInput.jsx                 # Input with loading state
        │   ├── ScoreCard.jsx                # Animated score gauge
        │   ├── CategoryBreakdown.jsx        # Bar chart per category
        │   ├── IssueList.jsx                # Search + filter + list
        │   └── IssueCard.jsx                # Issue with AI suggestion card
        └── services/
            └── api.js                       # axios wrappers
```

---

## 🔧 Adding a New Rule

1. Create a class in `src/main/java/.../rules/` implementing `Rule`:

```java
@Slf4j
@Component
public class MyNewRule implements Rule {

    @Override
    public List<Issue> check(Document doc) {
        List<Issue> issues = new ArrayList<>();

        for (Element el : doc.select("your-selector")) {
            if (/* condition */) {
                issues.add(Issue.builder()
                        .type("My Issue Type")
                        .message("Descriptive message about the problem")
                        .severity(Severity.HIGH)
                        .category(Category.STRUCTURE)
                        .element(el.outerHtml())
                        .suggestion("How to fix this issue")
                        .build());
            }
        }

        log.debug("MyNewRule found {} issues", issues.size());
        return issues;
    }
}
```

2. That's it — Spring auto-detects the `@Component` and adds it to the scan pipeline.

---

## 📝 Configuration Reference

| Property | File | Description |
|---|---|---|
| `server.port` | `application.properties` | API server port (default `7070`) |
| `gemini.api.key` | `application-secrets.properties` | Google Gemini API key |
| `spring.config.import` | `application.properties` | Pulls in secrets file at startup |

> 💡 `application-secrets.properties` should be in `.gitignore`. Never commit your API key.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-rule-name`
3. Implement your changes following the patterns above
4. Open a Pull Request with a clear description of what accessibility gap it addresses

---

<div align="center">

Built with ❤️ using **Java**, **Spring Boot** & **React**

*Making the web accessible, one scan at a time.*

</div>
