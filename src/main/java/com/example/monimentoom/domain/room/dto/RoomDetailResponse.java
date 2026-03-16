package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.comments.dto.CommentResponse;
import com.example.monimentoom.domain.room.model.Room;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record RoomDetailResponse(
        Long roomId,
        String name,
        String userProfileImageUrl,
        String nickname,
        Boolean isLoggedIn,
        Boolean isMine,
        Boolean isLiked,
        Long likeCount,
        Long commentCount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime userCreatedAt,
        List<CommentResponse> comments
) {
    public static RoomDetailResponse from(Room room, String userProfileImageUrl, boolean isLoggedIn, boolean isMine, boolean isLiked, long likeCount, long commentCount, List<CommentResponse> comments) {
        return new RoomDetailResponse(
                room.getId(),
                room.getName(),
                userProfileImageUrl,
                room.getUser().getNickname(),
                isLoggedIn,
                isMine,
                isLiked,
                likeCount,
                commentCount,
                room.getUser().getCreatedAt(),
                comments
        );
    }
}
