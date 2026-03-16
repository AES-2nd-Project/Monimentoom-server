package com.example.monimentoom.domain.comments.dto;

import com.example.monimentoom.domain.comments.model.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String content
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getUser().getProfileImageUrl(),
                comment.getCreatedAt(),
                comment.getContent()
        );
    }
}
