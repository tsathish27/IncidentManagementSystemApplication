package com.example.IncidentManagementSystemApplication.repository;

import com.example.IncidentManagementSystemApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    long countByRole(User.Role role);
}

