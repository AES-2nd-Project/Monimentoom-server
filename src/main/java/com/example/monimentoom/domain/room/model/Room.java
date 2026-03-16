package com.example.monimentoom.domain.room.model;

import com.example.monimentoom.domain.common.BaseTimeEntity;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
@Builder
public class Room extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String name;
    @Column
    private String frameImageUrl;
    @Column
    private String easelImageUrl;

    public void validateOwnership(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_ACCESS_DENIED);
        }
    }

    public void update(String name, Boolean updateImages, String frameImageUrl, String easelImageUrl) {
        if (name != null && !name.isBlank()) this.name = name;
        if (Boolean.TRUE.equals(updateImages)) {
            this.frameImageUrl = frameImageUrl;
            this.easelImageUrl = easelImageUrl;
        }
    }
}
