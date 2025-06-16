package com.example.IncidentManagementSystemApplication.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String role; // EMPLOYEE, AGENT, ADMIN
}


