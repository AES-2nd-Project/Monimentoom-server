package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RoomUpdateRequest {
    @NotBlank(message = "방 이름은 필수입니다.")
    private String name;
}
