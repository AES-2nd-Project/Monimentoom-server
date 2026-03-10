package com.example.monimentoom.domain.comments.repository;

import com.example.monimentoom.domain.comments.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT c from Comment c
            JOIN FETCH c.user
            WHERE c.room.id = :roomId
            """)
    List<Comment> findByRoomIdWithUser(Long roomId);
}
