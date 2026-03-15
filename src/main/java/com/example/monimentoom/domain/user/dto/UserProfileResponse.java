package com.example.monimentoom.domain.user.dto;

import com.example.monimentoom.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String description;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .description(user.getDescription())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
