package com.victor.lamontagne_api.service;

import com.victor.lamontagne_api.model.dto.UserDTO;
import com.victor.lamontagne_api.model.pojo.User;
import com.victor.lamontagne_api.repository.UserRepository;
import com.victor.lamontagne_api.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    private UserServiceImpl userService;

    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "Test User";
    private static final String USER_ID = "user123";
    private static final String TOKEN = "jwt.test.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, jwtService);
    }

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        // Given
        User user = createUser();
        String hashedPassword = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());
        user.setPassword(hashedPassword);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(USER_ID)).thenReturn(TOKEN);

        // When
        String result = userService.authenticate(EMAIL, PASSWORD);

        // Then
        assertEquals(TOKEN, result);
        verify(userRepository).findByEmail(EMAIL);
        verify(jwtService).generateToken(USER_ID);
    }

    @Test
    void register_ShouldCreateAndReturnUser_WhenEmailDoesNotExist() {
        // Given
        UserDTO inputDto = new UserDTO();
        inputDto.setEmail(EMAIL);
        inputDto.setPassword(PASSWORD);
        inputDto.setName(NAME);

        User savedUser = createUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserDTO result = userService.register(inputDto);

        // Then
        assertEquals(USER_ID, result.getId());
        assertEquals(EMAIL, result.getEmail());
        assertEquals(NAME, result.getName());
        assertNull(result.getPassword());
        verify(userRepository).save(any(User.class));
    }

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setName(NAME);
        return user;
    }

}