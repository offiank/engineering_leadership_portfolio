package com.ankur.req2task.api;

import com.ankur.req2task.dto.ErrorResponse;
import com.ankur.req2task.dto.PlanResponse;
import com.ankur.req2task.ingest.DocumentParserFactory;
import com.ankur.req2task.model.ProjectPlan;
import com.ankur.req2task.planning.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Main API controller. Two endpoints:
 * - POST /api/plan (text input)
 * - POST /api/plan/upload (file input: txt, pdf, docx)
 *
 * Kept intentionally thin — all logic lives in services.
 */
@RestController
@RequestMapping("/api")
public class PlanController {

    private static final Logger log = LoggerFactory.getLogger(PlanController.class);

    private final PlanService planService;
    private final DocumentParserFactory parserFactory;

    public PlanController(PlanService planService, DocumentParserFactory parserFactory) {
        this.planService = planService;
        this.parserFactory = parserFactory;
    }

    /**
     * Accepts raw text requirements.
     */
    @PostMapping("/plan")
    public ResponseEntity<?> planFromText(@RequestBody TextRequest request) {
        if (request.text() == null || request.text().isBlank()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.of("Invalid input", "Text body must not be empty"));
        }

        try {
            ProjectPlan plan = planService.generatePlan(request.text());
            return ResponseEntity.ok(PlanResponse.success(plan));
        } catch (PlanService.PlanGenerationException e) {
            log.error("Plan generation failed", e);
            return ResponseEntity.status(422).body(
                    ErrorResponse.of("Processing error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.internalServerError().body(
                    ErrorResponse.of("Internal error", "An unexpected error occurred"));
        }
    }

    /**
     * Accepts file upload: txt, pdf, docx.
     */
    @PostMapping(value = "/plan/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> planFromFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.of("Invalid input", "File is empty"));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !parserFactory.isSupported(filename)) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.of("Unsupported format",
                            "Supported: txt, md, pdf, docx. Got: " + filename));
        }

        try {
            String requirementsText = parserFactory.parse(filename, file.getBytes());
            log.info("Parsed file '{}' → {} chars", filename, requirementsText.length());

            if (requirementsText.isBlank()) {
                return ResponseEntity.badRequest().body(
                        ErrorResponse.of("Empty document", "No text could be extracted from the file"));
            }

            ProjectPlan plan = planService.generatePlan(requirementsText);
            return ResponseEntity.ok(PlanResponse.success(plan));

        } catch (Exception e) {
            log.error("File processing failed for {}", filename, e);
            return ResponseEntity.badRequest().body(
                    ErrorResponse.of("Parse error", e.getMessage()));
        }
    }

    /**
     * Simple text request body record.
     */
    public record TextRequest(String text) {
    }
}
