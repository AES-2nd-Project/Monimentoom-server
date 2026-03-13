package com.example.monimentoom.domain.like.repository;

import com.example.monimentoom.domain.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByRoomId(Long roomId);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    void removeByUserIdAndRoomId(Long userId, Long roomId);
}
