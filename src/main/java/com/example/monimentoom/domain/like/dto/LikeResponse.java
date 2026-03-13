package com.example.monimentoom.domain.like.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponse {
    private Long likeCount;
    private Boolean isLiked;
}
