package com.ankur.req2task.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Selects a single {@link ChatModel} bean at startup based on the
 * {@code ai.provider} property (set via the {@code AI_PROVIDER} env var).
 *
 * <p>Supported values: {@code ollama} (default), {@code openai}, {@code anthropic}.
 *
 * <p>All three Spring AI starters are on the classpath, but Spring AI's
 * auto-configuration creates provider-specific beans (OllamaChatModel,
 * OpenAiChatModel, AnthropicChatModel). This class picks the right one
 * and exposes it as the primary {@code ChatModel} for injection throughout
 * the application, keeping every other class provider-agnostic.
 */
@Configuration
public class AiProviderConfig {

    private static final Logger log = LoggerFactory.getLogger(AiProviderConfig.class);

    // ── Ollama (default) ──────────────────────────────────────────────────────

    @Bean
    @Primary
    @ConditionalOnProperty(name = "ai.provider", havingValue = "ollama", matchIfMissing = true)
    public ChatModel ollamaChatModelProvider(OllamaChatModel ollamaChatModel,
            @Value("${spring.ai.ollama.chat.options.model:llama3.1}") String model) {
        log.info("AI provider: Ollama | model: {}", model);
        return ollamaChatModel;
    }

    // ── OpenAI ────────────────────────────────────────────────────────────────

    @Bean
    @Primary
    @ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
    public ChatModel openAiChatModelProvider(OpenAiChatModel openAiChatModel,
            @Value("${spring.ai.openai.chat.options.model:gpt-4o}") String model) {
        log.info("AI provider: OpenAI | model: {}", model);
        return openAiChatModel;
    }

    // ── Anthropic (Claude) ────────────────────────────────────────────────────

    @Bean
    @Primary
    @ConditionalOnProperty(name = "ai.provider", havingValue = "anthropic")
    public ChatModel anthropicChatModelProvider(AnthropicChatModel anthropicChatModel,
            @Value("${spring.ai.anthropic.chat.options.model:claude-3-5-sonnet-20241022}") String model) {
        log.info("AI provider: Anthropic | model: {}", model);
        return anthropicChatModel;
    }
}
