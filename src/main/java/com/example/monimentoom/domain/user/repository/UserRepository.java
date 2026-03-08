package com.example.monimentoom.domain.user.repository;

import com.example.monimentoom.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
