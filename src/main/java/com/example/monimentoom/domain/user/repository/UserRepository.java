package com.example.monimentoom.domain.user.repository;

import com.example.monimentoom.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
