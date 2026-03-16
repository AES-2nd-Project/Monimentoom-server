package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.NotBlank;

public record RoomCreateRequest(
        @NotBlank(message = "방 이름은 필수입니다.")
        String name
) {}
