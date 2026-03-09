package com.example.monimentoom.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;

    public static UserResponse from ( Long id, String email, String nickname) {
        return UserResponse.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
