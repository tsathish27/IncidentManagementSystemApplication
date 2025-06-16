package com.example.IncidentManagementSystemApplication.service;

import com.example.IncidentManagementSystemApplication.model.Incident;
import com.example.IncidentManagementSystemApplication.model.User;
import com.example.IncidentManagementSystemApplication.repository.IncidentRepository;
import com.example.IncidentManagementSystemApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private UserRepository userRepository;

    public Incident createIncident(String title, String description, String createdByUsername) {
        User creator = userRepository.findByUsername(createdByUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Auto-assign to agent with least open incidents
        List<User> agents = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.AGENT)
                .toList();
        User assignedAgent = agents.stream()
                .min(Comparator.comparingLong(agent -> incidentRepository.findByAssignedTo(agent).stream()
                        .filter(i -> i.getStatus().equals("OPEN")).count()))
                .orElseThrow(() -> new RuntimeException("No agents available"));
        Incident incident = Incident.builder()
                .title(title)
                .description(description)
                .createdBy(creator)
                .assignedTo(assignedAgent)
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .slaDeadline(LocalDateTime.now().plusHours(4)) // Example SLA: 4 hours
                .escalated(false)
                .build();
        return incidentRepository.save(incident);
    }

    public List<Incident> getIncidentsForUser(User user) {
        if (user.getRole() == User.Role.EMPLOYEE) {
            return incidentRepository.findByCreatedBy(user);
        } else if (user.getRole() == User.Role.AGENT) {
            return incidentRepository.findByAssignedTo(user);
        } else {
            return incidentRepository.findAll();
        }
    }

    public Incident updateIncidentStatus(Long id, String status, String agentUsername) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        if (!incident.getAssignedTo().getUsername().equals(agentUsername)) {
            throw new RuntimeException("Not authorized to update this incident");
        }
        incident.setStatus(status);
        incident.setUpdatedAt(LocalDateTime.now());
        // Escalate if past SLA
        if (incident.getSlaDeadline().isBefore(LocalDateTime.now()) && !incident.isEscalated()) {
            incident.setEscalated(true);
        }
        return incidentRepository.save(incident);
    }

    public void checkAndEscalateBreaches() {
        List<Incident> openIncidents = incidentRepository.findAll().stream()
                .filter(i -> i.getStatus().equals("OPEN") && !i.isEscalated())
                .toList();
        for (Incident incident : openIncidents) {
            if (incident.getSlaDeadline().isBefore(LocalDateTime.now())) {
                incident.setEscalated(true);
                incidentRepository.save(incident);
            }
        }
    }

    public long countOpen() {
        return incidentRepository.countByStatus("OPEN");
    }
    public long countResolved() {
        return incidentRepository.countByStatus("RESOLVED");
    }
    public long countBreaches() {
        return incidentRepository.countByEscalatedTrue();
    }
}

