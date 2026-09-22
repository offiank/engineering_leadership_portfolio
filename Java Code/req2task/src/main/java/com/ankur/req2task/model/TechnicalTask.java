package com.ankur.req2task.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TechnicalTask(
        @JsonProperty("task_id") String taskId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("component") String component,
        @JsonProperty("priority") Priority priority,
        @JsonProperty("estimated_effort") String estimatedEffort,
        @JsonProperty("dependencies") String dependencies) {
    public enum Priority {
        CRITICAL, HIGH, MEDIUM, LOW
    }
}
