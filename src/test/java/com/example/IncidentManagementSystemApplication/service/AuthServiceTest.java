package com.example.IncidentManagementSystemApplication.service;

import com.example.IncidentManagementSystemApplication.dto.AuthResponse;
import com.example.IncidentManagementSystemApplication.dto.LoginRequest;
import com.example.IncidentManagementSystemApplication.dto.RegisterRequest;
import com.example.IncidentManagementSystemApplication.model.User;
import com.example.IncidentManagementSystemApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setRole("EMPLOYEE");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordHasher.hash("password")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        assertDoesNotThrow(() -> authService.register(request));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setRole("EMPLOYEE");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));
        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        User user = User.builder().username("testuser").passwordHash("hashed").role(User.Role.EMPLOYEE).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHasher.hash("password")).thenReturn("hashed");
        when(jwtUtil.generateToken("testuser", "EMPLOYEE")).thenReturn("token");
        AuthResponse response = authService.login(request);
        assertEquals("token", response.getToken());
        assertEquals("EMPLOYEE", response.getRole());
    }

    @Test
    void login_invalidUsername_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nouser");
        request.setPassword("password");
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_invalidPassword_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrong");
        User user = User.builder().username("testuser").passwordHash("hashed").role(User.Role.EMPLOYEE).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHasher.hash("wrong")).thenReturn("notHashed");
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}

