package com.ankur.req2task.planning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromptBuilderTest {

    private PromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new PromptBuilder();
    }

    @Test
    void testBuildUserPrompt() {
        String reqs = "Build a fast and secure login system.";
        String prompt = promptBuilder.buildUserPrompt(reqs);
        
        assertTrue(prompt.contains(reqs));
        assertTrue(prompt.contains("Analyze the following software requirements"));
        assertTrue(prompt.contains("Produce the JSON now."));
    }
}
