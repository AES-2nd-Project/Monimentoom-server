package com.example.monimentoom.domain.comments.controller;

import com.example.monimentoom.domain.comments.dto.CommentCreateRequest;
import com.example.monimentoom.domain.comments.dto.CommentResponse;
import com.example.monimentoom.domain.comments.dto.CommentUpdateRequest;
import com.example.monimentoom.domain.comments.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(request));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<CommentResponse>> getRoomComments(@PathVariable Long roomId) {
        return ResponseEntity.ok(commentService.getRoomComments(roomId));
    }

    @PatchMapping
    public ResponseEntity<CommentResponse> updateComment(@Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(commentService.updateComment(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
