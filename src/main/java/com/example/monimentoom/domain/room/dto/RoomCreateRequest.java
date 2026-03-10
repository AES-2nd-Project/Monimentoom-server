package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RoomCreateRequest {
    @NotBlank(message = "방 이름은 필수입니다.")
    private String name;
}
