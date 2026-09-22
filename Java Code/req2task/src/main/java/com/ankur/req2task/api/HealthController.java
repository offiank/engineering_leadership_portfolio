package com.ankur.req2task.api;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check that verifies the Ollama connection is alive.
 * Catches "model not pulled" and "Ollama not running" early,
 * which are the two most common startup failures.
 */
@RestController
public class HealthController {

    private final ChatModel chatModel;
    private final String modelName;

    public HealthController(ChatModel chatModel,
            @Value("${spring.ai.ollama.chat.model:llama3.1}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        try {
            // Simple ping — if this throws, Ollama isn't reachable
            chatModel.call("ping");
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "model", modelName,
                    "ollama", "connected"));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                    "status", "DOWN",
                    "model", modelName,
                    "error", "Cannot reach Ollama. Is it running? Did you pull the model?",
                    "hint", "Run: docker compose up -d && ollama pull " + modelName));
        }
    }
}
