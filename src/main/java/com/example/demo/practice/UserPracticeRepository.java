package com.example.demo.practice;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Practice repository for user queries.
 */
public interface UserPracticeRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
