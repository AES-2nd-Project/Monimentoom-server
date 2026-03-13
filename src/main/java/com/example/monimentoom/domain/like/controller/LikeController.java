package com.example.monimentoom.domain.like.controller;

import com.example.monimentoom.domain.like.dto.LikeResponse;
import com.example.monimentoom.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/{roomId}")
    public ResponseEntity<LikeResponse> getLikes(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        return ResponseEntity.ok(likeService.getLikes(userId, roomId));
    }

    // 내가 좋아요한 방 모아보기는 과하겠죠?
    @PostMapping("/{roomId}")
    public ResponseEntity<Void> addLike(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        likeService.addLike(userId, roomId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> removeLike(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        likeService.deleteLike(userId, roomId);
        return ResponseEntity.noContent().build();
    }
}
