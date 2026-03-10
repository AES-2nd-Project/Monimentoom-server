package com.example.monimentoom.domain.user.controller;

import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.user.dto.UserLoginRequest;
import com.example.monimentoom.domain.user.dto.UserSignupRequest;
import com.example.monimentoom.domain.user.dto.UserResponse;
import com.example.monimentoom.domain.user.service.UserService;
import com.example.monimentoom.global.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserResponse userResponse = userService.loginUser(request);
        String token = jwtUtil.createToken(userResponse.getId());
        // TODO : refresh token 생성 및 저장
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Long userId) {
        userService.logoutUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/main/{roomId}")
    public ResponseEntity<RoomResponse> updateMainRoom(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId) {
        return ResponseEntity.ok(userService.updateMainRoom(userId, roomId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id
    ) {
        userService.deleteUser(userId, id);
        return ResponseEntity.noContent().build();
    }

}
