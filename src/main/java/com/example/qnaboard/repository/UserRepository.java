package com.example.qnaboard.repository;

import com.example.qnaboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    Optional<String> findEmailByName(String name);
    Optional<User> findByPassword(String password);
    User findByProviderId(String providerId);
}
