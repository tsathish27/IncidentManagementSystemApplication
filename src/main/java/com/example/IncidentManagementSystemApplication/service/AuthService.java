package com.example.IncidentManagementSystemApplication.service;

import com.example.IncidentManagementSystemApplication.dto.AuthResponse;
import com.example.IncidentManagementSystemApplication.dto.LoginRequest;
import com.example.IncidentManagementSystemApplication.dto.RegisterRequest;
import com.example.IncidentManagementSystemApplication.model.User;
import com.example.IncidentManagementSystemApplication.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordHasher passwordHasher;
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordHasher.hash(request.getPassword()))
                .role(User.Role.valueOf(request.getRole().toUpperCase()))
                .build();
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }
        User user = userOpt.get();
        String hashed = passwordHasher.hash(request.getPassword());
        if (!user.getPasswordHash().equals(hashed)) {
            throw new RuntimeException("Invalid username or password");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name());
    }
}

