package com.example.monimentoom.domain.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateRequest {
    // TODO: jwt 토큰으로 인증할 수 있으면 id 빼기
    private Long id;
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
