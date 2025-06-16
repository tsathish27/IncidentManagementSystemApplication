package com.example.IncidentManagementSystemApplication.repository;

import com.example.IncidentManagementSystemApplication.model.Incident;
import com.example.IncidentManagementSystemApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByCreatedBy(User user);
    List<Incident> findByAssignedTo(User user);
    long countByStatus(String status);
    long countByEscalatedTrue();
}

