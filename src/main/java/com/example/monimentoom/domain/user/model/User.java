package com.example.monimentoom.domain.user.model;

import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;
    @Column(nullable = false, unique = true)
    private String email;
    private String profileImageUrl;
    @Column(unique = true)
    private Long kakaoId;
    private String description;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    @Column(nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @JsonIgnore
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

    /**
     * 프로필 이미지 제거 시 '' 빈문자열 보내도록 처리
     */
    public void updateUserProfile(String nickname, String description, String profileImageUrl) {
        if (nickname != null) this.nickname = nickname;
        if (description != null) this.description = description;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl.isBlank() ? null : profileImageUrl;
    }
}
