package com.example.monimentoom.domain.room.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {
    Long id;
    Long userId;
    String roomName;
}
