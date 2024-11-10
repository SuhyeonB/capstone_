package com.engdiarytoon.server.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
    Optional<User> findByEmail(String email);
    // method to check if a user with a specific email exists
    boolean existsByEmail(String email);
}