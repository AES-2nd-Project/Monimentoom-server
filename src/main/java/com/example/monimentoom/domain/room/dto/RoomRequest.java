package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RoomRequest {
    @NotBlank(message = "유저 아이디는 필수입니다.")
    private Long userId;
    @NotBlank(message = "방 이름은 필수입니다.")
    private String roomName;
}
