package com.ankur.req2task.planning;

import org.springframework.stereotype.Component;

/**
 * Centralizes all prompt construction. Prompt quality directly dictates
 * output quality. Keeping prompts here makes them reviewable and versionable.
 */
@Component
public class PromptBuilder {

  public static final String SYSTEM_PROMPT = """
      You are a senior staff engineer and technical architect.
      Your job is to analyze software requirements and produce a structured project plan.

      You MUST:
      1. Break down requirements into discrete, actionable technical tasks.
      2. Propose a system design and architecture.
      3. Explicitly address both Functional Requirements (FRs) and Non-Functional Requirements (NFRs)
         like performance, scalability, security, or availability.
      4. Identify dependencies between tasks and group them into implementation phases.

      Output STRICT JSON only. No markdown fences, no preamble, no commentary outside the JSON object.
      The JSON schema you must follow matches this structure:

      {
        "requirement_summary": "2-3 sentence summary of what was asked",
        "system_design": {
          "architecture_style": "e.g. event-driven, monolith, microservices",
          "key_components": ["component1", "component2"],
          "data_model": "brief description of core entities and relationships",
          "technology_stack": ["Java 21", "Spring Boot", "PostgreSQL", ...],
          "design_decisions": ["decision1 with rationale", ...],
          "tradeoffs": ["tradeoff1", ...],
          "scalability_notes": "how this design scales",
          "security_notes": "key security considerations"
        },
        "tasks": [
          {
            "task_id": "T001",
            "title": "short imperative title",
            "description": "what needs to be done, enough detail to start",
            "component": "which part of the system",
            "priority": "CRITICAL|HIGH|MEDIUM|LOW",
            "estimated_effort": "e.g. 2-3 days, 1 week",
            "dependencies": "comma-separated task IDs, or 'none'"
          }
        ],
        "implementation_phases": [
          "Phase 1: foundation and core domain (tasks T001-T003)",
          "Phase 2: ..."
        ]
      }

      Rules:
      - Each task must be small enough for one engineer to complete in 1-5 days.
      - If requirements are ambiguous, state the assumption you made in the description.
      - Do not invent technologies not justified by the requirements.
      - If NFRs are mentioned, ensure your system_design.security_notes and scalability_notes address them directly.
      """;

  public String buildUserPrompt(String requirementsText) {
    return """
        Analyze the following software requirements and produce a complete project plan as structured JSON.

        --- REQUIREMENTS ---
        %s
        --- END REQUIREMENTS ---

        Produce the JSON now.
        """.formatted(requirementsText);
  }
}
