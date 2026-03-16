package com.example.monimentoom.domain.like.dto;

public record LikeResponse(
        Long likeCount,
        Boolean isLiked
) {}
