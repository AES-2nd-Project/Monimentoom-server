package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RoomUpdateRequest {
    @Size(min = 1)
    private String name;
    private Boolean updateImages; // true일 때만 이미지 업데이트 (null/false → 이미지 유지)
    private String frameImageUrl;
    private String easelImageUrl;
}
