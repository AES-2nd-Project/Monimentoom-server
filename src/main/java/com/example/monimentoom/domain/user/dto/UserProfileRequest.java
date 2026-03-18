package com.example.monimentoom.domain.user.dto;

import jakarta.validation.constraints.Size;

public record UserProfileRequest(
        @Size(min = 1, max = 20)
        String nickname,
        String profileImageUrl,
        @Size(min = 1, max = 255)
        String description
) {}
