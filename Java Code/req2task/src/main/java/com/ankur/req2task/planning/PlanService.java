package com.ankur.req2task.planning;

import com.ankur.req2task.guardrails.InjectionDetector;
import com.ankur.req2task.guardrails.InputValidator;
import com.ankur.req2task.model.ProjectPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private static final Logger log = LoggerFactory.getLogger(PlanService.class);

    private final ChatModel chatModel;
    private final PromptBuilder promptBuilder;
    private final OutputValidator outputValidator;
    private final InputValidator inputValidator;
    private final InjectionDetector injectionDetector;

    @Value("${spring.ai.ollama.chat.model:llama3.1}")
    private String modelName;

    public PlanService(ChatModel chatModel, PromptBuilder promptBuilder,
            OutputValidator outputValidator, InputValidator inputValidator,
            InjectionDetector injectionDetector) {
        this.chatModel = chatModel;
        this.promptBuilder = promptBuilder;
        this.outputValidator = outputValidator;
        this.inputValidator = inputValidator;
        this.injectionDetector = injectionDetector;
    }

    public ProjectPlan generatePlan(String requirementsText) {
        // 1. Check for prompt injection attacks
        List<String> injectionFindings = injectionDetector.detect(requirementsText);
        if (!injectionFindings.isEmpty()) {
            log.warn("Blocked request due to injection findings: {}", injectionFindings);
            throw new PlanGenerationException("Input rejected due to potential prompt injection.");
        }

        // 2. Validate that the requirements are complete
        List<String> validationIssues = inputValidator.validate(requirementsText);
        if (!validationIssues.isEmpty()) {
            log.warn("Input validation failed: {}", validationIssues);
            throw new PlanGenerationException("Input validation failed: " + String.join("; ", validationIssues));
        }

        log.info("Generating plan from {} chars of requirements", requirementsText.length());

        // 3. Build the prompt
        Message systemMessage = new SystemMessage(PromptBuilder.SYSTEM_PROMPT);
        Message userMessage = new UserMessage(promptBuilder.buildUserPrompt(requirementsText));
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // 4. Call the model
        ChatResponse response;
        try {
            response = chatModel.call(prompt);
        } catch (Exception e) {
            log.error("Model call failed", e);
            throw new PlanGenerationException("Model call failed: " + e.getMessage(), e);
        }

        String rawOutput = response.getResult().getOutput().getContent();

        // 5. Validate and parse the output
        ProjectPlan plan = outputValidator.validateAndParse(rawOutput, modelName);

        log.info("Plan generated: {} tasks, model={}",
                plan.tasks() != null ? plan.tasks().size() : 0,
                plan.modelUsed());

        return plan;
    }

    public static class PlanGenerationException extends RuntimeException {
        public PlanGenerationException(String message) {
            super(message);
        }

        public PlanGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
