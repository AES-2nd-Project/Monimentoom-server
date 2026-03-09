package com.example.monimentoom.domain.user.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.monimentoom.domain.user.model.User;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test/users")
@RequiredArgsConstructor
public class TestUserController {
    private final TestUserRepository testUserRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(testUserRepository.findAll());
    }
}
