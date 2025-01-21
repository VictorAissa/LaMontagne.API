package com.victor.lamontagne_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.lamontagne_api.model.dto.UserDTO;
import com.victor.lamontagne_api.model.pojo.LoginRequest;
import com.victor.lamontagne_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserService userService;
    private UserController userController;

    private static final String BASE_URL = "/api/user";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NAME = "Test User";

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void login_ShouldReturnToken() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        String expectedToken = "test.jwt.token";

        when(userService.authenticate(TEST_EMAIL, TEST_PASSWORD)).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + expectedToken + "\""));
    }

    @Test
    void register_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserDTO inputUser = new UserDTO(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);

        UserDTO createdUser = new UserDTO();
        createdUser.setId("userId123");
        createdUser.setEmail(TEST_EMAIL);
        createdUser.setName(TEST_NAME);

        when(userService.register(any(UserDTO.class))).thenReturn(createdUser);

        when(userService.register(any(UserDTO.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("userId123"))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}