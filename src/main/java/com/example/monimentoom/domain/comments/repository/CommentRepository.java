package com.example.monimentoom.domain.comments.repository;

import com.example.monimentoom.domain.comments.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
