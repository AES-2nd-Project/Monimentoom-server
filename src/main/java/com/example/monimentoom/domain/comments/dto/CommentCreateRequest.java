package com.example.monimentoom.domain.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {
    private Long userId;
    private Long roomId;
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
