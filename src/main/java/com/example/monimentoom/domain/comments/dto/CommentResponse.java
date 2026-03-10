package com.example.monimentoom.domain.comments.dto;

import com.example.monimentoom.domain.comments.model.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String nickname;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String content;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .nickname(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .content(comment.getContent())
                .build();
    }
}
