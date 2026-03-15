package com.example.monimentoom.domain.room.model;

import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String name;
    @Column
    private String frameImageUrl;
    @Column
    private String easelImageUrl;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    @Column(nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public void validateOwnership(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_ACCESS_DENIED);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 부분 수정 가능하도록 함
    public void update(String name, String frameImageUrl, String easelImageUrl) {
        if (name != null) this.name = name;
        if (frameImageUrl != null) this.frameImageUrl = frameImageUrl;
        if (easelImageUrl != null) this.easelImageUrl = easelImageUrl;
    }
}
