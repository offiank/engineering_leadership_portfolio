package com.ankur.req2task.api;

import com.ankur.req2task.ingest.DocumentParserFactory;
import com.ankur.req2task.model.ProjectPlan;
import com.ankur.req2task.planning.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanService planService;

    @MockBean
    private DocumentParserFactory parserFactory;

    @Test
    void testPlanFromTextSuccess() throws Exception {
        String reqs = "Valid reqs";
        PlanController.TextRequest req = new PlanController.TextRequest(reqs);

        ProjectPlan plan = new ProjectPlan("summary", null, null, null, "model", null);
        when(planService.generatePlan(reqs)).thenReturn(plan);

        mockMvc.perform(post("/api/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requirementSummary").value("summary"));
    }

    @Test
    void testPlanFromTextEmpty() throws Exception {
        PlanController.TextRequest req = new PlanController.TextRequest(" ");

        mockMvc.perform(post("/api/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testPlanFromFileSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "file content".getBytes());

        when(parserFactory.isSupported("test.txt")).thenReturn(true);
        when(parserFactory.parse(eq("test.txt"), any(byte[].class))).thenReturn("parsed content");
        
        ProjectPlan plan = new ProjectPlan("file summary", null, null, null, "model", null);
        when(planService.generatePlan("parsed content")).thenReturn(plan);

        mockMvc.perform(multipart("/api/plan/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testPlanFromFileUnsupported() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xyz", "text/plain", "file content".getBytes());

        when(parserFactory.isSupported("test.xyz")).thenReturn(false);

        mockMvc.perform(multipart("/api/plan/upload")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
