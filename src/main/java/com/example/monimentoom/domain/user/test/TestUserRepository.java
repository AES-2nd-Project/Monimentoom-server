package com.example.monimentoom.domain.user.test;

import com.example.monimentoom.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepository extends JpaRepository<User, Long> {
}
