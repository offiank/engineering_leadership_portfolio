package com.ankur.req2task.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TechnicalTask(
        @JsonProperty("task_id") String taskId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("component") String component,
        @JsonProperty("priority") Priority priority,
        @JsonProperty("estimated_effort") String estimatedEffort,
        @JsonProperty("dependencies") String dependencies) {
    public enum Priority {
        CRITICAL, HIGH, MEDIUM, LOW;

        @JsonCreator
        public static Priority fromString(String value) {
            if (value == null) return MEDIUM;
            try {
                return Priority.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return MEDIUM;
            }
        }
    }
}
