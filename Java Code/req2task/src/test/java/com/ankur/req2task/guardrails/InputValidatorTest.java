package com.ankur.req2task.guardrails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    private InputValidator inputValidator;

    @BeforeEach
    void setUp() {
        inputValidator = new InputValidator();
    }

    @Test
    void testValidInput() {
        String validReqs = "We need a new user registration system. " +
                "Functional requirements include user signup, login, and profile editing. " +
                "Non-functional requirements include high availability, strong security, " +
                "and it must support high throughput for user registrations.";
        List<String> issues = inputValidator.validate(validReqs);
        assertTrue(issues.isEmpty(), "Valid input should not have any issues");
    }

    @Test
    void testEmptyInput() {
        List<String> issues = inputValidator.validate("");
        assertFalse(issues.isEmpty());
        assertTrue(issues.contains("Requirements text is empty."));
    }

    @Test
    void testNullInput() {
        List<String> issues = inputValidator.validate(null);
        assertFalse(issues.isEmpty());
        assertTrue(issues.contains("Requirements text is empty."));
    }

    @Test
    void testTooShortInput() {
        String shortReqs = "Build a functional login system with security.";
        List<String> issues = inputValidator.validate(shortReqs);
        assertFalse(issues.isEmpty());
        assertTrue(issues.contains("Requirements text is too short. Please provide a complete specification."));
    }

    @Test
    void testMissingFunctional() {
        String reqs = "The system must have high performance, scalability, security, availability, and reliability. " +
                "It needs to handle latency and throughput well for maintainability purposes.";
        List<String> issues = inputValidator.validate(reqs);
        assertFalse(issues.isEmpty());
        assertTrue(issues.contains("Missing Functional Requirements (FRs): Please specify core features and user stories."));
    }

    @Test
    void testMissingNonFunctional() {
        String reqs = "The system must include functional features like user login, user logout, password reset, " +
                "and the ability to update user profiles with new images and preferences. User stories have been gathered.";
        List<String> issues = inputValidator.validate(reqs);
        assertFalse(issues.isEmpty());
        assertTrue(issues.contains("Missing Non-Functional Requirements (NFRs): Please specify performance, security, or scalability constraints."));
    }
}
