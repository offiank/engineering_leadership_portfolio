package com.ankur.req2task.planning;

import com.ankur.req2task.model.ProjectPlan;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OutputValidatorTest {

    private OutputValidator outputValidator;

    @BeforeEach
    void setUp() {
        outputValidator = new OutputValidator(new ObjectMapper());
    }

    @Test
    void testExtractJsonStripsMarkdown() {
        String raw = "```json\n{\"test\":\"value\"}\n```";
        JsonNode node = outputValidator.extractJson(raw);
        assertEquals("value", node.get("test").asText());
    }

    @Test
    void testExtractJsonWithCommentary() {
        String raw = "Here is the JSON:\n{\"test\":\"value\"}\nHope it helps.";
        JsonNode node = outputValidator.extractJson(raw);
        assertEquals("value", node.get("test").asText());
    }

    @Test
    void testExtractJsonInvalid() {
        String raw = "This is not JSON.";
        assertThrows(OutputValidator.OutputValidationException.class, () -> outputValidator.extractJson(raw));
    }

    @Test
    void testValidateStructureMissingSummaryIsWarningNotCritical() throws Exception {
        // requirement_summary is missing but tasks/system_design are also missing here,
        // which ARE critical — so this case still reports both critical and warning issues.
        String raw = "{\"tasks\": []}";
        JsonNode node = new ObjectMapper().readTree(raw);
        OutputValidator.ValidationResult result = outputValidator.validateStructure(node);

        assertTrue(result.warnings().contains("Missing or empty requirement_summary"));
        assertTrue(result.critical().contains("Missing system_design object"));
        assertTrue(result.critical().contains("Missing or empty tasks array"));
    }

    @Test
    void testValidateAndParseThrowsOnCriticalIssues() {
        // Missing system_design and tasks entirely — should now fail loudly
        // instead of silently returning an incomplete plan.
        String raw = "{\"requirement_summary\": \"Just a summary, nothing else\"}";

        assertThrows(OutputValidator.OutputValidationException.class,
                () -> outputValidator.validateAndParse(raw, "llama3.1"));
    }

    @Test
    void testValidateAndParseSuccess() {
        String raw = """
                {
                  "requirement_summary": "Test summary",
                  "system_design": {
                    "architecture_style": "monolith",
                    "key_components": ["auth"],
                    "technology_stack": ["Java"],
                    "data_model": "test model",
                    "design_decisions": [],
                    "tradeoffs": [],
                    "scalability_notes": "ok",
                    "security_notes": "ok"
                  },
                  "tasks": [
                    {
                      "task_id": "T001",
                      "title": "Setup",
                      "description": "Do setup",
                      "component": "auth",
                      "priority": "HIGH",
                      "estimated_effort": "1 day",
                      "dependencies": "none"
                    }
                  ],
                  "implementation_phases": []
                }
                """;

        ProjectPlan plan = outputValidator.validateAndParse(raw, "llama3.1");
        assertNotNull(plan);
        assertEquals("Test summary", plan.requirementSummary());
        assertEquals("llama3.1", plan.modelUsed());
        assertNotNull(plan.tokenUsage());
        assertEquals(1, plan.tasks().size());
    }
}
