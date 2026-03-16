package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.room.model.Room;

public record RoomBasicResponse(
        Long roomId,
        String nickname,
        String userProfileImageUrl,
        String name
) {
    public static RoomBasicResponse from(Room room) {
        if (room == null) return null;
        return new RoomBasicResponse(
                room.getId(),
                room.getUser().getNickname(),
                room.getUser().getProfileImageUrl(),
                room.getName()
        );
    }
}
