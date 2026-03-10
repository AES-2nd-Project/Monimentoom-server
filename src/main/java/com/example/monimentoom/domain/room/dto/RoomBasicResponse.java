package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.room.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBasicResponse {
    private Long id;
    private Long userId;
    private String name;

    public static RoomBasicResponse from(Room room) {
        if (room == null) return null;
        return RoomBasicResponse.builder()
                .id(room.getId())
                .userId(room.getUser().getId())
                .name(room.getName())
                .build();
    }
}
