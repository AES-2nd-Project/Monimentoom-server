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
    private Long roomId;
    private String nickname;
    private String name;

    public static RoomBasicResponse from(Room room) {
        if (room == null) return null;
        return RoomBasicResponse.builder()
                .roomId(room.getId())
                .nickname(room.getUser().getNickname())
                .name(room.getName())
                .build();
    }
}
