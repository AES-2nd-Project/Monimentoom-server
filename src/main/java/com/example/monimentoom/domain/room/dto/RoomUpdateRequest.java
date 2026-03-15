package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RoomUpdateRequest {
    @Size(min = 1)
    private String name;
    private String frameImageUrl;
    private String easelImageUrl;
}
