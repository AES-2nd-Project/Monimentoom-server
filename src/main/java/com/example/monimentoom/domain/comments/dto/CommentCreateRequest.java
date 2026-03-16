package com.example.monimentoom.domain.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull(message = "방 아이디는 필수입니다.")
        Long roomId,
        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content
) {}
