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
    private Long roomId;
    private String name;
    private String userProfileImageUrl;
    private String nickname;
    private boolean isLoggedIn;
    private boolean isMine;
    private boolean isLiked;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userCreatedAt;
    private List<CommentResponse> comments;

    public static RoomDetailResponse from(Room room, String userProfileImageUrl, boolean isLoggedIn, boolean isMine, boolean isLiked, List<CommentResponse> comments) {
        return RoomDetailResponse.builder()
                .roomId(room.getId())
                .name(room.getName())
                .userProfileImageUrl(userProfileImageUrl)
                .nickname(room.getUser().getNickname())
                .isLoggedIn(isLoggedIn)
                .isMine(isMine)
                .isLiked(isLiked)
                .userCreatedAt(room.getUser().getCreatedAt())
                .comments(comments)
                .build();
    }
}
