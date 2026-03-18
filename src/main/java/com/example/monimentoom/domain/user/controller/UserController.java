package com.example.monimentoom.domain.user.controller;

import com.example.monimentoom.domain.room.dto.RoomBasicResponse;
import com.example.monimentoom.domain.user.dto.*;
import com.example.monimentoom.domain.user.service.UserService;
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

    /** 사용자 자신의 메인룸 수정하기 */
    @PatchMapping("/main-room/{roomId}")
    public ResponseEntity<RoomBasicResponse> updateMainRoom(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId) {
        return ResponseEntity.ok(userService.updateMainRoom(userId, roomId));
    }

    /** 회원 탈퇴 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id
    ) {
        userService.deleteUser(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    /** 프로필 수정 */
    @PatchMapping("/profile/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserProfileRequest request
            ){
            return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

}
