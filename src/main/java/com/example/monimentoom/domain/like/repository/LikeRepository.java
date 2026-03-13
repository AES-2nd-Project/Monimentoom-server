package com.example.monimentoom.domain.like.repository;

import com.example.monimentoom.domain.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByRoomId(Long roomId);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    long deleteByUserIdAndRoomId(Long userId, Long roomId);

    @Query("""
            SELECT l FROM Like l
            JOIN FETCH l.room
            JOIN FETCH l.user
            WHERE l.user.id = :userId
            """)
    List<Like> findByUserId(Long userId);
}
