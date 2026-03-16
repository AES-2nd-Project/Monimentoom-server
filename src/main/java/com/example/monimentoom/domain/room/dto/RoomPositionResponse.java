package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.room.model.Room;

import java.util.List;

public record RoomPositionResponse(
        Long roomId,
        Long userId,
        String nickname,
        String name,
        String frameImageUrl,
        String easelImageUrl,
        List<PositionResponse> positions
) {
    public static RoomPositionResponse from(Room room, List<PositionResponse> positions) {
        if (room == null) return null;
        return new RoomPositionResponse(
                room.getId(),
                room.getUser().getId(),
                room.getUser().getNickname(),
                room.getName(),
                room.getFrameImageUrl(),
                room.getEaselImageUrl(),
                positions
        );
    }
}
