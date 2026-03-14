package com.example.monimentoom.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileRequest {
    private String nickname;
    private String profileImageUrl;
    private String description;
}
