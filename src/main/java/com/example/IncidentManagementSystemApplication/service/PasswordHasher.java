package com.example.IncidentManagementSystemApplication.service;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class PasswordHasher {
    public String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found");
        }
    }
}

