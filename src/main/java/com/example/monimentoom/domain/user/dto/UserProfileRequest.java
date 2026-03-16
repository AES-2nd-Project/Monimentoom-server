package com.example.monimentoom.domain.user.dto;

import jakarta.validation.constraints.Size;

public record UserProfileRequest(
        @Size(min = 1, max = 20)
        // TODO: 악의적 입력 방지 필요
        String nickname,
        String profileImageUrl,
        @Size(min = 1, max = 255)
        // TODO: 악의적 입력 방지 필요
        String description
) {}
