package com.example.monimentoom.domain.user.repository;

import com.example.monimentoom.domain.user.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NotBlank(message = "이메일은 필수입니다.") String email);
}
