package com.ankur.req2task.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SystemDesign(
        @JsonProperty("architecture_style") String architectureStyle,
        @JsonProperty("key_components") List<String> keyComponents,
        @JsonProperty("data_model") String dataModel,
        @JsonProperty("technology_stack") List<String> technologyStack,
        @JsonProperty("design_decisions") List<String> designDecisions,
        @JsonProperty("tradeoffs") List<String> tradeoffs,
        @JsonProperty("scalability_notes") String scalabilityNotes,
        @JsonProperty("security_notes") String securityNotes) {
}
