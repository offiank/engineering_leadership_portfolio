package com.ankur.req2task.dto;

import com.ankur.req2task.model.ProjectPlan;

/**
 * Wrapper for successful responses. Keeps the API contract
 * clean — success and error shapes are distinct.
 */
public record PlanResponse(
        boolean success,
        ProjectPlan plan) {
    public static PlanResponse success(ProjectPlan plan) {
        return new PlanResponse(true, plan);
    }
}
