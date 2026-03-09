package com.example.monimentoom.domain.user.controller;

import com.example.monimentoom.domain.user.dto.UserLoginRequest;
import com.example.monimentoom.domain.user.dto.UserSignupRequest;
import com.example.monimentoom.domain.user.dto.UserResponse;
import com.example.monimentoom.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserSignupRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    // todo : 추후 jwt 토큰으로부터 userId 추출하여 처리
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Long userId){
        userService.logoutUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/main/{roomId}")
    public ResponseEntity<Void> updateMainRoom(@PathVariable Long roomId) {
        userService.updateMainRoom(roomId);
        return ResponseEntity.noContent().build();
    }

}
