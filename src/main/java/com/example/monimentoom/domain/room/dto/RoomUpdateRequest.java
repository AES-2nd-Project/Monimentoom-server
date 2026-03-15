package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RoomUpdateRequest {
    private String name;
    private String frameImageUrl;
    private String easelImageUrl;
}
