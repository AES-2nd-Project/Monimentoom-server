package com.example.monimentoom.domain.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPageResponse {
    private List<CommentResponse> comments;
    private Long nextCursorId; // 다음 요청 때 쓸 커서(null이면 마지막 페이지)
    private boolean hasNext; // 다음 있는지: 프론트에서 무한 스크롤 종료 여부 결정
}
