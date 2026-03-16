package com.example.monimentoom.domain.room.dto;

import jakarta.validation.constraints.Size;

public record RoomUpdateRequest(
        @Size(min = 1)
        String name,
        Boolean updateImages, // true일 때만 이미지 업데이트 (null/false → 이미지 유지)
        String frameImageUrl,
        String easelImageUrl
) {}
