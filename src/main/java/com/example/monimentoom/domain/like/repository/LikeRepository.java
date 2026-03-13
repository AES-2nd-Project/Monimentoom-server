package com.example.monimentoom.domain.like.repository;

import com.example.monimentoom.domain.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByRoomId(Long roomId);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    long deleteByUserIdAndRoomId(Long userId, Long roomId);
}
