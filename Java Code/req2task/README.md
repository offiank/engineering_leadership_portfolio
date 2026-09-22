# req2task

`req2task` is a Spring Boot application that leverages Spring AI and Ollama to automatically convert unstructured software requirements into a structured project plan with discrete technical tasks.

## Features

- **Requirements Ingestion**: Upload requirements as raw text, PDF, or DOCX files.
- **AI-Powered Planning**: Uses local LLMs (via Ollama) to analyze requirements, propose a system architecture, and break the work down into actionable tasks.
- **Guardrails**: Built-in prompt injection detection and input validation to ensure requirements are sufficiently detailed (checking for both functional and non-functional requirements).
- **Structured Output**: Enforces strict JSON output from the LLM, mapping directly into strongly typed Java models.

## Tech Stack

- **Java 21**
- **Spring Boot 3.3.4**
- **Spring AI (1.0.0-M3)**
- **Ollama** (Local LLM execution, defaults to `llama3.1`)
- **Apache PDFBox** & **Apache POI** (for document parsing)

## Prerequisites

1. **Java 21**: Make sure you have JDK 21 installed.
2. **Maven**: For building and running the project.
3. **Ollama**: You must have Ollama running locally to execute the models. You can either install it directly or run it via Docker Compose.
   
   **Option A: Install directly**
   - [Install Ollama](https://ollama.com/)
   - Pull the required model: `ollama pull llama3.1`

   **Option B: Using Docker Compose**
   - Ensure Docker is installed.
   - Run the provided `docker-compose.yml`:
     ```bash
     docker compose up -d
     ```
   - Then execute into the container to pull the model:
     ```bash
     docker exec -it ollama ollama pull llama3.1
     ```

## Running the Application

1. Start Ollama and ensure the model is pulled.
2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   Or using standard maven:
   ```bash
   mvn spring-boot:run
   ```

The application will start on port `8080`.

## API Endpoints

### 1. Health Check
Checks if the application and Ollama connection are healthy.
```bash
curl http://localhost:8080/health
```

### 2. Plan from Text
Submit raw text requirements to generate a project plan.
```bash
curl -X POST http://localhost:8080/api/plan \
  -H "Content-Type: application/json" \
  -d '{
    "text": "We need a scalable user authentication system with SSO, rate limiting, and role-based access control. Performance and high availability are critical."
  }'
```

### 3. Plan from File
Upload a file (`.txt`, `.md`, `.pdf`, `.docx`) containing requirements.
```bash
curl -X POST http://localhost:8080/api/plan/upload \
  -F "file=@/path/to/your/requirements.pdf"
```

## Running Tests

The project includes a comprehensive test suite (Unit and Integration tests). Run the tests using:
```bash
mvn test
```
