package com.example.monimentoom.domain.comments.dto;

import java.util.List;

public record CommentPageResponse(
        List<CommentResponse> comments,
        Long nextCursorId, // 다음 요청 때 쓸 커서(null이면 마지막 페이지)
        boolean hasNext    // 다음 있는지: 프론트에서 무한 스크롤 종료 여부 결정
) {}
