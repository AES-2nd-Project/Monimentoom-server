package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.comments.dto.CommentResponse;
import com.example.monimentoom.domain.room.model.Room;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailResponse {
    private Long id;
    private String name;
    // TODO: 프로필 이미지 url 추가해야함
    private String nickname;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userCreatedAt;
    private List<CommentResponse> comments;

    public static RoomDetailResponse from(Room room, List<CommentResponse> comments) {
        return RoomDetailResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .nickname(room.getUser().getNickname())
                .userCreatedAt(room.getUser().getCreatedAt())
                .comments(comments)
                .build();
    }
}
