package com.example.IncidentManagementSystemApplication.controller;

import com.example.IncidentManagementSystemApplication.dto.AuthResponse;
import com.example.IncidentManagementSystemApplication.dto.LoginRequest;
import com.example.IncidentManagementSystemApplication.dto.RegisterRequest;
import com.example.IncidentManagementSystemApplication.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "Endpoints for user registration and login.")
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Operation(summary = "Register a new user", description = "Registers a new user with username, password, and role.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @Operation(summary = "Login and get JWT token", description = "Authenticates a user and returns a JWT token and role if successful.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
