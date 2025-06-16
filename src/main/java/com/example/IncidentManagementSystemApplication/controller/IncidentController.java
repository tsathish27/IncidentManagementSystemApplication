package com.example.IncidentManagementSystemApplication.controller;

import com.example.IncidentManagementSystemApplication.model.Incident;
import com.example.IncidentManagementSystemApplication.model.User;
import com.example.IncidentManagementSystemApplication.repository.UserRepository;
import com.example.IncidentManagementSystemApplication.service.IncidentService;
import com.example.IncidentManagementSystemApplication.service.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Incident Management", description = "Endpoints for managing incidents and dashboard stats.")
@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:4200")
public class IncidentController {
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private Claims getClaimsFromHeader(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) throw new RuntimeException("Missing or invalid Authorization header");
        return jwtUtil.getClaimsFromToken(token);
    }

    @Operation(summary = "Create a new incident", description = "Creates a new incident for the authenticated user.")
    @PostMapping("/incidents")
    public ResponseEntity<Map<String, Object>> createIncident(@RequestBody Map<String, String> req, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Claims claims = getClaimsFromHeader(authHeader);
        String username = claims.getSubject();
        Incident incident = incidentService.createIncident(req.get("title"), req.get("description"), username);
        return ResponseEntity.ok(Map.of(
            "message", "Incident created successfully",
            "incident", incident
        ));
    }

    @Operation(summary = "Get all incidents for user", description = "Fetches all incidents for the authenticated user.")
    @GetMapping("/incidents")
    public ResponseEntity<Map<String, Object>> getIncidents(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Claims claims = getClaimsFromHeader(authHeader);
        String username = claims.getSubject();
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Incident> incidents = incidentService.getIncidentsForUser(user);
        return ResponseEntity.ok(Map.of(
            "message", "Incidents fetched successfully",
            "incidents", incidents
        ));
    }

    @Operation(summary = "Update incident status", description = "Updates the status of an incident. Only the assigned agent or admin can update.")
    @PutMapping("/incidents/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> req, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Claims claims = getClaimsFromHeader(authHeader);
        String username = claims.getSubject();
        Incident incident = incidentService.updateIncidentStatus(id, req.get("status"), username);
        return ResponseEntity.ok(Map.of(
            "message", "Incident status updated successfully",
            "incident", incident
        ));
    }

    @Operation(summary = "Get dashboard stats", description = "Fetches dashboard statistics for admin view.")
    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("open", incidentService.countOpen());
        stats.put("resolved", incidentService.countResolved());
        stats.put("breaches", incidentService.countBreaches());
        return ResponseEntity.ok(Map.of(
            "message", "Dashboard stats fetched successfully",
            "stats", stats
        ));
    }
}
