package com.example.monimentoom.domain.comments.model;

import com.example.monimentoom.domain.common.BaseTimeEntity;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "comments")
@Builder
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String content;

    public void validateOwnership(Long userId){
        if (!this.user.getId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }
    }

    public void update(String content) {
        this.content = content;
    }
}
