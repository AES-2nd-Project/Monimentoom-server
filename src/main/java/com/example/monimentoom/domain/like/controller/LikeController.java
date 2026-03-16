package com.example.monimentoom.domain.like.controller;

import com.example.monimentoom.domain.like.dto.LikeResponse;
import com.example.monimentoom.domain.like.service.LikeService;
import com.example.monimentoom.domain.room.dto.RoomBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/{roomId}")
    public ResponseEntity<LikeResponse> getLikes(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        return ResponseEntity.ok(likeService.getLikes(userId, roomId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<RoomBasicResponse>> getLikedRooms(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(likeService.getLikedRooms(userId));
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<LikeResponse> addLike(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likeService.addLike(userId, roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<LikeResponse> removeLike(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        return ResponseEntity.ok(likeService.deleteLike(userId, roomId));
    }
}
