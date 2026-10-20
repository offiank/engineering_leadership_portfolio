# Requirements-to-Design Generator

`req2task` is a Spring Boot application that uses **Spring AI** to automatically convert unstructured software requirements into a structured project plan with discrete, actionable technical tasks.

It supports multiple AI providers — run locally for free with **Ollama**, or plug in **OpenAI** (GPT-4o) or **Anthropic** (Claude) via a single environment variable. No code changes required to switch.

---

## What It Does

- **Multi-Provider AI** — Switch between Ollama (local), OpenAI, and Anthropic at runtime via `AI_PROVIDER` env var.
- **Requirements Ingestion** — Submit requirements as raw text, PDF, DOCX, or Markdown files.
- **AI-Powered Planning** — The model analyzes requirements, proposes a system architecture, and breaks the work into actionable tasks with effort estimates and dependencies.
- **Guardrails** — Built-in prompt injection detection and input validation (checks for functional *and* non-functional requirements).
- **Structured Output** — Enforces strict JSON from the LLM, mapping directly into strongly-typed Java records.

---

## Tech Stack

| Component | Version |
|---|---|
| Java | 25 (LTS) |
| Spring Boot | 3.5.16 |
| Spring AI | 1.0.0 GA |
| Ollama | local (default) |
| OpenAI | GPT-4o (optional) |
| Anthropic | Claude 3.5 Sonnet (optional) |
| Apache PDFBox | 3.0.8 |
| Apache POI | 5.5.1 |

---

## Prerequisites

- **JDK 21+** (JDK 25 recommended to match `pom.xml`)
- **Maven 3.9+**
- One of the following AI providers (see below)

## Configuration (.env)

You can manage all keys and provider settings in a single `.env` file. The application automatically reads `.env` on startup:

```bash
# Create your .env from the example template
cp .env.example .env
```

Edit `.env` to set your keys:
```properties
AI_PROVIDER=openai              # ollama | openai | anthropic
OPENAI_API_KEY=sk-proj-...
ANTHROPIC_API_KEY=sk-ant-...
```

---

## AI Provider Setup

### Option 1 — Ollama (local, free, default)

No API key needed. Runs entirely on your machine.

**Install directly:**
```bash
# Install: https://ollama.com/
ollama pull llama3.1
```

**Or via Docker Compose:**
```bash
docker compose --profile ollama up -d
docker exec -it ollama ollama pull llama3.1
```

**Run the app:**
```bash
mvn spring-boot:run
# AI_PROVIDER defaults to "ollama"
```

---

### Option 2 — OpenAI (GPT-4o)

```bash
export OPENAI_API_KEY=sk-...
export AI_PROVIDER=openai

mvn spring-boot:run
```

To use a different model (e.g. `gpt-4o-mini`):
```bash
export OPENAI_MODEL=gpt-4o-mini
```

---

### Option 3 — Anthropic (Claude)

```bash
export ANTHROPIC_API_KEY=sk-ant-...
export AI_PROVIDER=anthropic

mvn spring-boot:run
```

To use a different model (e.g. `claude-3-haiku-20240307`):
```bash
export ANTHROPIC_MODEL=claude-3-haiku-20240307
```

---

### Provider comparison

| Provider | Cost | Privacy | JSON quality | Context window |
|---|---|---|---|---|
| Ollama | Free | 100% local | Good | Model-dependent |
| OpenAI GPT-4o | Pay-per-token | Cloud | Excellent | 128K tokens |
| Anthropic Claude 3.5 Sonnet | Pay-per-token | Cloud | Excellent | 200K tokens |

---

## Running with Docker Compose

Three Compose profiles are available — one per provider:

```bash
# Ollama (local)
docker compose --profile ollama up

# OpenAI
OPENAI_API_KEY=sk-... docker compose --profile openai up

# Anthropic
ANTHROPIC_API_KEY=sk-ant-... docker compose --profile anthropic up
```

---

## API Reference

The application starts on port `8080` by default (override with `PORT` env var).

### `GET /health`

Verifies the active AI provider is reachable.

```bash
curl http://localhost:8080/health
```

**Response (UP):**
```json
{
  "status": "UP",
  "provider": "openai",
  "model": "openai/gpt-4o"
}
```

---

### `POST /api/plan` — Plan from text

Submit raw requirements as JSON.

```bash
curl -X POST http://localhost:8080/api/plan \
  -H "Content-Type: application/json" \
  -d '{
    "text": "We need a scalable user authentication system with SSO, rate limiting, and role-based access control. Performance and high availability are critical."
  }'
```

---

### `POST /api/plan/upload` — Plan from file

Upload a `.txt`, `.md`, `.pdf`, or `.docx` file.

```bash
curl -X POST http://localhost:8080/api/plan/upload \
  -F "file=@/path/to/requirements.pdf"
```

**Supported formats:** `txt`, `md`, `pdf`, `docx` (max 10 MB)

---

### Response schema

```json
{
  "success": true,
  "plan": {
    "requirement_summary": "2-3 sentence summary",
    "system_design": {
      "architecture_style": "microservices",
      "key_components": ["API Gateway", "Auth Service"],
      "technology_stack": ["Java 25", "Spring Boot", "PostgreSQL"],
      "design_decisions": ["..."],
      "tradeoffs": ["..."],
      "scalability_notes": "...",
      "security_notes": "..."
    },
    "tasks": [
      {
        "task_id": "T001",
        "title": "Set up Auth Service skeleton",
        "description": "...",
        "component": "Auth Service",
        "priority": "CRITICAL",
        "estimated_effort": "2-3 days",
        "dependencies": "none"
      }
    ],
    "implementation_phases": [
      "Phase 1: Foundation (T001-T003)",
      "Phase 2: Core features (T004-T007)"
    ],
    "model_used": "openai/gpt-4o",
    "token_usage": {
      "prompt_tokens": 512,
      "completion_tokens": 1024,
      "total_tokens": 1536
    }
  }
}
```

---

## Running Tests

```bash
mvn test
```

The test suite covers unit tests for all guardrails, document parsers, prompt building, output validation, and API controllers.

---

## Environment Variables Reference

| Variable | Default | Description |
|---|---|---|
| `AI_PROVIDER` | `ollama` | Active provider: `ollama`, `openai`, `anthropic` |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama server URL |
| `OLLAMA_MODEL` | `llama3.1` | Ollama model name |
| `OPENAI_API_KEY` | *(required for openai)* | OpenAI API key |
| `OPENAI_MODEL` | `gpt-4o` | OpenAI model name |
| `ANTHROPIC_API_KEY` | *(required for anthropic)* | Anthropic API key |
| `ANTHROPIC_MODEL` | `claude-3-5-sonnet-20241022` | Anthropic model name |
| `PORT` | `8080` | Server port |
