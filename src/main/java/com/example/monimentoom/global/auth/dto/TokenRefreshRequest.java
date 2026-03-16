package com.example.monimentoom.global.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(@NotBlank String refreshToken,
                                  @NotBlank String deviceId) {
}
