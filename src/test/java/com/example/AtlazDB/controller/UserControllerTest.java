package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.UserRequestDTO;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setProfile(Profile.ADMIN);
        mockUser.setUserStatus(UserStatus.DISPONIVEL);
    }

    @Nested
    @DisplayName("Tests for GET endpoints")
    class GetEndpointsTests {

        @Test
        @DisplayName("Should return list of user response DTOs with status 200 OK")
        void shouldReturnAllUsers() throws Exception {
            when(userService.listAll()).thenReturn(List.of(mockUser));

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("John Doe"))
                    .andExpect(jsonPath("$[0].email").value("john@example.com"));

            verify(userService, times(1)).listAll();
        }

        @Test
        @DisplayName("Should return user response DTO when found by ID")
        void shouldFindUserById() throws Exception {
            when(userService.findById(1L)).thenReturn(Optional.of(mockUser));

            mockMvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("John Doe"));

            verify(userService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty or null when user not found by ID")
        void shouldReturnNotFoundWhenUserIdDoesNotExist() throws Exception {
            when(userService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/users/99"))
                    .andExpect(status().isOk());

            verify(userService, times(1)).findById(99L);
        }

        @Test
        @DisplayName("Should return user statuses enum values")
        void shouldGetUserStatuses() throws Exception {
            mockMvc.perform(get("/users/user-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Tests for POST, PUT and DELETE endpoints")
    class MutationEndpointsTests {

        @Test
        @DisplayName("Should create user successfully and return saved user")
        void shouldCreateUser() throws Exception {
            UserRequestDTO requestDTO = new UserRequestDTO();
            requestDTO.setName("Jane Doe");
            requestDTO.setEmail("jane@example.com");

            when(userService.save(any(UserRequestDTO.class))).thenReturn(mockUser);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("John Doe"));

            verify(userService, times(1)).save(any(UserRequestDTO.class));
        }

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUser() throws Exception {
            UserRequestDTO requestDTO = new UserRequestDTO();
            requestDTO.setName("John Updated");

            when(userService.update(eq(1L), any(UserRequestDTO.class))).thenReturn(mockUser);

            mockMvc.perform(put("/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(userService, times(1)).update(eq(1L), any(UserRequestDTO.class));
        }

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUser() throws Exception {
            doNothing().when(userService).delete(1L);

            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isOk());

            verify(userService, times(1)).delete(1L);
        }
    }
}