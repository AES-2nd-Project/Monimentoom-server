package com.example.monimentoom.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
