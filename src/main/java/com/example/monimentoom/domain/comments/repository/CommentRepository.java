package com.example.monimentoom.domain.comments.repository;

import com.example.monimentoom.domain.comments.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT c from Comment c
            JOIN FETCH c.user
            WHERE c.room.id = :roomId
            """)
    List<Comment> findByRoomIdWithUser(@Param("roomId") Long roomId);

    // c.id < :cursorId : 마지막으로 본 댓글보다 이전(오래된)것들 가져옴
    // :cursorId IS NULL : 첫 요청(처음 로딩)일 때는 조건 없이 최신순으로
    // Pageable pageable : 몇개씩 가져올지 제한(ex. size+1: 11개) -> limit 쿼리를 Spring JPA가 알아서 넣어줌!
    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.user
            WHERE c.room.id = :roomId AND
            (:cursorId IS NULL OR c.id < :cursorId)
            ORDER BY c.id DESC
            """)
    List<Comment> findByRoomIdWithCursor(
            @Param("roomId") Long roomId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
