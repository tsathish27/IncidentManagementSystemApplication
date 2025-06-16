package com.example.IncidentManagementSystemApplication.repository;

import com.example.IncidentManagementSystemApplication.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}

