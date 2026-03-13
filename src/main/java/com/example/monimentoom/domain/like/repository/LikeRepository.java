package com.example.monimentoom.domain.like.repository;

import com.example.monimentoom.domain.like.model.Like;
import com.example.monimentoom.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByRoomId(Long roomId);

    Boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    Long deleteByUserIdAndRoomId(Long userId, Long roomId);
}
