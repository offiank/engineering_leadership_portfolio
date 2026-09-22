package com.ankur.req2task.guardrails;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Detects common prompt injection patterns in untrusted input.
 * This is a heuristic first-line defense. It does not guarantee
 * that all injections are caught, but it blocks the most common
 * "ignore previous instructions" and obfuscation attacks.
 */
@Component
public class InjectionDetector {

    // Patterns covering common jailbreak and override attempts
    private static final List<Pattern> INJECTION_PATTERNS = List.of(
            Pattern.compile("ignore (all )?previous (instructions|prompts)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("you are now (in )?(developer|jailbreak|DAN) mode", Pattern.CASE_INSENSITIVE),
            Pattern.compile("reveal (your|the) (system prompt|instructions|rules)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("do not follow your rules", Pattern.CASE_INSENSITIVE),
            Pattern.compile("act as if you have no restrictions", Pattern.CASE_INSENSITIVE),
            Pattern.compile("disregard (all )?prior (instructions|constraints)", Pattern.CASE_INSENSITIVE));

    /**
     * Analyzes the input text for potential injection attacks.
     * Returns a list of findings. An empty list means no issues detected.
     */
    public List<String> detect(String input) {
        List<String> findings = new ArrayList<>();
        if (input == null || input.isBlank()) {
            return findings;
        }

        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                findings.add("Potential prompt injection detected: matched pattern '" + pattern.pattern() + "'");
            }
        }

        // Simple heuristic for base64/obfuscation: long strings of valid base64 chars
        // with no spaces
        if (input.length() > 100 && input.matches("^[A-Za-z0-9+/=]{100,}$")) {
            findings.add("Potential obfuscated payload (base64-like string) detected.");
        }

        return findings;
    }
}
