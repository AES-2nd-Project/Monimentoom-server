package com.example.monimentoom.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileRequest {
    @Size(min = 1, max = 20)
    @Pattern(regexp = ".*\\S.*")
    private String nickname;
    private String profileImageUrl;
    @Size(min = 1, max = 255)
    @Pattern(regexp = ".*\\S.*")
    private String description;
}
