package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.room.model.Room;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomPositionResponse {
    private Long id;
    private Long userId;
    private String name;
    private List<PositionResponse> positioins;

    public static RoomPositionResponse from(Room room, List<PositionResponse> positions) {
        if (room == null) return null;
        return RoomPositionResponse.builder()
                .id(room.getId())
                .userId(room.getUser().getId())
                .name(room.getName())
                .positioins(positions)
                .build();
    }
}
