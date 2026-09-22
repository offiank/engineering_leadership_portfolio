package com.ankur.req2task.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProjectPlan(
    @JsonProperty("requirement_summary") String requirementSummary,
    @JsonProperty("system_design") SystemDesign systemDesign,
    @JsonProperty("tasks") List<TechnicalTask> tasks,
    @JsonProperty("implementation_phases") List<String> implementationPhases,
    @JsonProperty("model_used") String modelUsed,
    @JsonProperty("token_usage") TokenUsage tokenUsage
) {
    public record TokenUsage(
        @JsonProperty("prompt_tokens") int promptTokens,
        @JsonProperty("completion_tokens") int completionTokens,
        @JsonProperty("total_tokens") int totalTokens
    ) {}
}
