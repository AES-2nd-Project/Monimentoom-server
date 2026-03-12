package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.room.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomPositionResponse {
    private Long roomId;
    private Long userId;
    private String name;
    private List<PositionResponse> positions;

    public static RoomPositionResponse from(Room room, List<PositionResponse> positions) {
        if (room == null) return null;
        return RoomPositionResponse.builder()
                .roomId(room.getId())
                .userId(room.getUser().getId())
                .name(room.getName())
                .positions(positions)
                .build();
    }
}
