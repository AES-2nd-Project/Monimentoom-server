package com.example.monimentoom.domain.user.dto;

import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private Long mainRoomId;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                // mainRoom이 null일 경우 safe 하게 처리
                .mainRoomId(Optional.ofNullable(user.getMainRoom())
                        .map(Room::getId)
                        .orElse(null))
                .build();
    }
}
