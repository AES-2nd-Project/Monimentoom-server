package com.example.monimentoom.domain.comments.service;

import com.example.monimentoom.domain.comments.dto.CommentCreateRequest;
import com.example.monimentoom.domain.comments.dto.CommentResponse;
import com.example.monimentoom.domain.comments.dto.CommentUpdateRequest;
import com.example.monimentoom.domain.comments.model.Comment;
import com.example.monimentoom.domain.comments.repository.CommentRepository;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final RoomRepository roomRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        Comment comment = Comment.builder()
                .user(user)
                .room(room)
                .content(request.getContent())
                .build();
        return CommentResponse.from(commentRepository.save(comment));
    }

    // 방에 대한 댓글 가져오기
    @Transactional(readOnly = true)
    public List<CommentResponse> getRoomComments(Long roomId) {
        if (!roomRepository.existsById(roomId)) throw new CustomException(ErrorCode.ROOM_NOT_FOUND);

        return commentRepository.findByRoomIdWithUser(roomId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        commentRepository.deleteById(id);
    }


}
