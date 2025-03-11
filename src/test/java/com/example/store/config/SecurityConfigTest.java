package com.example.store.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("GET /api/health should be accessible without authentication")
  void healthCheck_shouldBePublic() throws Exception {
    mockMvc.perform(get("/api/health"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /products should require authentication")
  void getProducts_shouldRequireAuthentication() throws Exception {
    mockMvc.perform(get("/products"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("GET /products should be accessible with USER role")
  void getProducts_shouldBeAccessibleWithUserRole() throws Exception {
    mockMvc.perform(get("/products"))
        .andExpect(status().isOk());
  }
}