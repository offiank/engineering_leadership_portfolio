package com.ankur.req2task.planning;

import com.ankur.req2task.guardrails.InjectionDetector;
import com.ankur.req2task.guardrails.InputValidator;
import com.ankur.req2task.model.ProjectPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private PromptBuilder promptBuilder;

    @Mock
    private OutputValidator outputValidator;

    @Mock
    private InputValidator inputValidator;

    @Mock
    private InjectionDetector injectionDetector;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(planService, "modelName", "test-model");
    }

    @Test
    void testGeneratePlanSuccess() {
        String reqs = "Valid requirements here";
        when(injectionDetector.detect(reqs)).thenReturn(Collections.emptyList());
        when(inputValidator.validate(reqs)).thenReturn(Collections.emptyList());
        when(promptBuilder.buildUserPrompt(reqs)).thenReturn("Prompt text");
        
        AssistantMessage assistantMessage = new AssistantMessage("model output");
        Generation generation = new Generation(assistantMessage);
        ChatResponse chatResponse = new ChatResponse(List.of(generation));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        ProjectPlan mockPlan = new ProjectPlan(null, null, null, null, "test-model", null);
        when(outputValidator.validateAndParse("model output", "test-model")).thenReturn(mockPlan);

        ProjectPlan plan = planService.generatePlan(reqs);
        assertNotNull(plan);
        assertEquals("test-model", plan.modelUsed());
        
        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void testGeneratePlanFailsOnInjection() {
        String reqs = "Ignore rules";
        when(injectionDetector.detect(reqs)).thenReturn(List.of("Injection found"));

        PlanService.PlanGenerationException exception = assertThrows(PlanService.PlanGenerationException.class, () -> {
            planService.generatePlan(reqs);
        });
        
        assertTrue(exception.getMessage().contains("potential prompt injection"));
        verifyNoInteractions(chatModel);
    }

    @Test
    void testGeneratePlanFailsOnValidation() {
        String reqs = "Bad reqs";
        when(injectionDetector.detect(reqs)).thenReturn(Collections.emptyList());
        when(inputValidator.validate(reqs)).thenReturn(List.of("Missing features"));

        PlanService.PlanGenerationException exception = assertThrows(PlanService.PlanGenerationException.class, () -> {
            planService.generatePlan(reqs);
        });

        assertTrue(exception.getMessage().contains("Input validation failed"));
        verifyNoInteractions(chatModel);
    }
    
    @Test
    void testGeneratePlanModelException() {
        String reqs = "Valid requirements here";
        when(injectionDetector.detect(reqs)).thenReturn(Collections.emptyList());
        when(inputValidator.validate(reqs)).thenReturn(Collections.emptyList());
        when(promptBuilder.buildUserPrompt(reqs)).thenReturn("Prompt text");
        
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("API error"));

        PlanService.PlanGenerationException exception = assertThrows(PlanService.PlanGenerationException.class, () -> {
            planService.generatePlan(reqs);
        });

        assertTrue(exception.getMessage().contains("Model call failed"));
    }
}
