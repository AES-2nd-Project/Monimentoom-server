package com.example.monimentoom.domain.user.dto;

import com.example.monimentoom.domain.user.model.User;

public record UserProfileResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        String description
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getDescription()
        );
    }
}
