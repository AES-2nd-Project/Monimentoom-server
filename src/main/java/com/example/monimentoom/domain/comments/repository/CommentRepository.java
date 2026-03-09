package com.example.monimentoom.domain.comments.repository;

import com.example.monimentoom.domain.comments.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
