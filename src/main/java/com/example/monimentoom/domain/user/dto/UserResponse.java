package com.example.monimentoom.domain.user.dto;

import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.user.model.User;

import java.util.Optional;

public record UserResponse(
        Long id,
        String nickname,
        Long mainRoomId
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                Optional.ofNullable(user.getMainRoom())
                        .map(Room::getId)
                        .orElse(null)
        );
    }
}
