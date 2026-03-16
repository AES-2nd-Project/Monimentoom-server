package com.example.monimentoom.domain.user.model;

import com.example.monimentoom.domain.common.BaseTimeEntity;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;
    private String profileImageUrl;
    @Column(unique = true)
    private Long kakaoId;
    private String description;

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_room_id")
    private Room mainRoom = null;

    public void validateOwnership(Long userId) {
        if (!this.id.equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    public void updateMainRoom(Room mainRoom) {
        this.mainRoom = mainRoom;
    }

    /** 프로필 이미지 제거 시 '' 빈문자열 보내도록 처리 */
    public void updateUserProfile(String nickname, String description, String profileImageUrl){
        if(nickname != null) this.nickname = nickname;
        if(description != null) this.description = description;
        if(profileImageUrl != null) this.profileImageUrl = profileImageUrl.isBlank() ? null : profileImageUrl;
    }
}
