package com.ankur.req2task.planning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ankur.req2task.model.ProjectPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the model's output before returning it to the caller.
 * Handles markdown fence stripping, schema validation, and deserialization.
 */
@Component
public class OutputValidator {

    private static final Logger log = LoggerFactory.getLogger(OutputValidator.class);
    private final ObjectMapper objectMapper;

    public OutputValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Extracts valid JSON from the model response. Local models sometimes wrap
     * JSON in markdown fences or add commentary despite instructions not to.
     */
    public JsonNode extractJson(String rawResponse) {
        String cleaned = stripMarkdownFences(rawResponse).trim();

        try {
            return objectMapper.readTree(cleaned);
        } catch (Exception e) {
            log.debug("Direct JSON parse failed, attempting extraction");
        }

        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) {
            String jsonSubstring = cleaned.substring(start, end + 1);
            try {
                return objectMapper.readTree(jsonSubstring);
            } catch (Exception e) {
                log.error("Failed to extract JSON even after substring.", e);
            }
        }

        throw new OutputValidationException("Model output was not valid JSON");
    }

    /**
     * Validates the structure of the parsed plan against the expected schema.
     */
    public List<String> validateStructure(JsonNode plan) {
        List<String> issues = new ArrayList<>();

        if (!plan.has("requirement_summary") || plan.get("requirement_summary").asText().isBlank()) {
            issues.add("Missing or empty requirement_summary");
        }

        if (!plan.has("system_design")) {
            issues.add("Missing system_design object");
        } else {
            JsonNode sd = plan.get("system_design");
            if (!sd.has("architecture_style"))
                issues.add("system_design missing architecture_style");
            if (!sd.has("key_components"))
                issues.add("system_design missing key_components");
            if (!sd.has("technology_stack"))
                issues.add("system_design missing technology_stack");
        }

        if (!plan.has("tasks") || !plan.get("tasks").isArray() || plan.get("tasks").isEmpty()) {
            issues.add("Missing or empty tasks array");
        } else {
            JsonNode tasks = plan.get("tasks");
            for (int i = 0; i < tasks.size(); i++) {
                JsonNode task = tasks.get(i);
                if (!task.has("task_id"))
                    issues.add("Task " + i + " missing task_id");
                if (!task.has("title"))
                    issues.add("Task " + i + " missing title");
                if (!task.has("description"))
                    issues.add("Task " + i + " missing description");
            }
        }

        return issues;
    }

    /**
     * Full validation pipeline: extract, validate, deserialize.
     */
    public ProjectPlan validateAndParse(String rawResponse, String modelUsed) {
        JsonNode json = extractJson(rawResponse);
        List<String> issues = validateStructure(json);

        if (!issues.isEmpty()) {
            log.warn("Output validation issues: {}", issues);
            // We still return the plan but log the issues. In production, you'd decide
            // whether to retry or fail based on severity.
        }

        try {
            ProjectPlan plan = objectMapper.treeToValue(json, ProjectPlan.class);
            return enrichWithMetadata(plan, modelUsed, rawResponse);
        } catch (Exception e) {
            throw new OutputValidationException("Failed to deserialize validated JSON: " + e.getMessage(), e);
        }
    }

    private ProjectPlan enrichWithMetadata(ProjectPlan plan, String modelUsed, String rawResponse) {
        // Rough heuristic for local models without token counts
        int promptEstimate = estimateTokens(rawResponse.length());
        int completionEstimate = estimateTokens(rawResponse.length());

        ProjectPlan.TokenUsage usage = new ProjectPlan.TokenUsage(
                promptEstimate, completionEstimate, promptEstimate + completionEstimate);

        return new ProjectPlan(
                plan.requirementSummary(),
                plan.systemDesign(),
                plan.tasks(),
                plan.implementationPhases(),
                modelUsed,
                usage);
    }

    private int estimateTokens(int charCount) {
        // Rough: 1 token ≈ 4 characters for English text
        return Math.max(1, charCount / 4);
    }

    private String stripMarkdownFences(String text) {
        String cleaned = text;
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned;
    }

    public static class OutputValidationException extends RuntimeException {
        public OutputValidationException(String message) {
            super(message);
        }

        public OutputValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
