package com.chubb.claims;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClaimsApiTest {
    @Autowired
    MockMvc mvc;

    @Test
    void unauthenticatedIsRejected() throws Exception {
        mvc.perform(get("/api/claims")).andExpect(status().isUnauthorized());
    }

    @Test
    void claimantCanCreateAndStaffCanViewDashboard() throws Exception {
        mvc.perform(post("/api/claims").with(httpBasic("claimant1", "demo123")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"MOTOR\",\"market\":\"SG\",\"description\":\"Collision\",\"estimatedAmount\":1500}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("SUBMITTED"));
        mvc.perform(get("/api/dashboard").with(httpBasic("officer1", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.exposure[0].currency").value("SGD"));
        mvc.perform(get("/api/dashboard").with(httpBasic("claimant1", "demo123"))).andExpect(status().isForbidden());
    }

    @Test
    void cannotSubmitInvalidClaim() throws Exception {
        mvc.perform(post("/api/claims").with(httpBasic("claimant1", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"MOTOR\",\"market\":\"SG\",\"description\":\"\",\"estimatedAmount\":-1}"))
                .andExpect(status().isBadRequest());
    }
}
