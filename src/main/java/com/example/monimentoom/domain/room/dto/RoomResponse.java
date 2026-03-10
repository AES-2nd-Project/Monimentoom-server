package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.room.model.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {
    private Long id;
    private Long userId;
    private String name;

    public static RoomResponse from(Room room) {
        if (room == null) return null;
        return RoomResponse.builder()
                .id(room.getId())
                .userId(room.getUser().getId())
                .name(room.getName())
                .build();
    }
}
