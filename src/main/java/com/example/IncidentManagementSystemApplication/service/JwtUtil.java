package com.example.IncidentManagementSystemApplication.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Generate a secure HS512 key once when the JwtUtil bean is created
    private final Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // Optional: expose the Base64 encoded key to store it somewhere or log it for future use
    public String getBase64Secret() {
        return java.util.Base64.getEncoder().encodeToString(signingKey.getEncoded());
    }

    private Key getSigningKey() {
        return signingKey;
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
