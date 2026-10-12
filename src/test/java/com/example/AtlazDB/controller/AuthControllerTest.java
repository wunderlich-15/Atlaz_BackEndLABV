package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.LoginRequestDTO;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @Nested
    @DisplayName("Tests for login endpoint")
    class LoginEndpointTests {

        @Test
        @DisplayName("Should return 401 Unauthorized when user is not found by email")
        void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            LoginRequestDTO requestDTO = new LoginRequestDTO();
            requestDTO.setEmail("notfound@example.com");
            requestDTO.setPassword("wrongpassword");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Credenciais inválidas"));
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when password does not match")
        void shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws Exception {
            LoginRequestDTO requestDTO = new LoginRequestDTO();
            requestDTO.setEmail("john@example.com");
            requestDTO.setPassword("wrongpassword");

            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setEmail("john@example.com");
            mockUser.setPasswordHash("correctpassword");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Credenciais inválidas"));
        }

        @Test
        @DisplayName("Should return 200 OK and login response when credentials are valid")
        void shouldReturnOkWhenCredentialsAreValid() throws Exception {
            LoginRequestDTO requestDTO = new LoginRequestDTO();
            requestDTO.setEmail("john@example.com");
            requestDTO.setPassword("secret123");

            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setName("John Doe");
            mockUser.setEmail("john@example.com");
            mockUser.setPasswordHash("secret123");
            mockUser.setProfile(Profile.ADMIN);

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.profile").value("ADMIN"));
        }
    }
}