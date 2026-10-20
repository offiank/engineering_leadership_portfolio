package com.ankur.req2task.api;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check that verifies the active AI provider is reachable.
 * Works with Ollama, OpenAI, and Anthropic — provider is selected
 * at startup via the AI_PROVIDER environment variable.
 */
@RestController
public class HealthController {

    private final ChatModel chatModel;

    @Value("${ai.provider:ollama}")
    private String provider;

    @Value("${spring.ai.ollama.chat.options.model:llama3.1}")
    private String ollamaModel;

    @Value("${spring.ai.openai.chat.options.model:gpt-4o}")
    private String openaiModel;

    @Value("${spring.ai.anthropic.chat.options.model:claude-3-5-sonnet-20241022}")
    private String anthropicModel;

    private String activeModel;

    public HealthController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostConstruct
    void resolveActiveModel() {
        activeModel = switch (provider.toLowerCase()) {
            case "openai"    -> "openai/" + openaiModel;
            case "anthropic" -> "anthropic/" + anthropicModel;
            default          -> "ollama/" + ollamaModel;
        };
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        try {
            chatModel.call("ping");
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "provider", provider,
                    "model", activeModel));
        } catch (Exception e) {
            String hint = switch (provider.toLowerCase()) {
                case "openai"    -> "Check that OPENAI_API_KEY is set and valid.";
                case "anthropic" -> "Check that ANTHROPIC_API_KEY is set and valid.";
                default          -> "Is Ollama running? Run: docker compose up -d && ollama pull " + ollamaModel;
            };
            return ResponseEntity.status(503).body(Map.of(
                    "status", "DOWN",
                    "provider", provider,
                    "model", activeModel,
                    "error", e.getMessage() != null ? e.getMessage() : "Connection failed",
                    "hint", hint));
        }
    }
}
