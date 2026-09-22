package com.ankur.req2task.guardrails;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Enforces rules before the text ever hits the LLM.
 * This validates that requirements are complete and safe.
 */
@Component
public class InputValidator {

    private static final Set<String> NFR_KEYWORDS = Set.of(
            "performance", "scalability", "security", "availability",
            "reliability", "latency", "throughput", "maintainability");

    private static final int MIN_LENGTH = 100;

    public List<String> validate(String requirementsText) {
        List<String> issues = new ArrayList<>();

        if (requirementsText == null || requirementsText.isBlank()) {
            issues.add("Requirements text is empty.");
            return issues;
        }

        if (requirementsText.length() < MIN_LENGTH) {
            issues.add("Requirements text is too short. Please provide a complete specification.");
        }

        // Ensure both Functional and Non-Functional requirements exist
        String lowerText = requirementsText.toLowerCase();
        boolean hasFunctional = lowerText.contains("functional") || lowerText.contains("features")
                || lowerText.contains("user stories");
        boolean hasNonFunctional = NFR_KEYWORDS.stream().anyMatch(lowerText::contains);

        if (!hasFunctional) {
            issues.add("Missing Functional Requirements (FRs): Please specify core features and user stories.");
        }
        if (!hasNonFunctional) {
            issues.add(
                    "Missing Non-Functional Requirements (NFRs): Please specify performance, security, or scalability constraints.");
        }

        return issues;
    }
}
