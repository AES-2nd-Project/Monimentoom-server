package com.example.monimentoom.domain.comments.service;

import com.example.monimentoom.domain.comments.dto.CommentCreateRequest;
import com.example.monimentoom.domain.comments.dto.CommentPageResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private static final int MAX_PAGE_SIZE = 100;
    private final RoomRepository roomRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    @Transactional
    public CommentResponse createComment(Long userId, CommentCreateRequest request) {
        User user = userRepository.findById(userId)
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

    @Transactional(readOnly = true)
    public CommentPageResponse getRoomCommentsCursor(Long roomId, Long cursorId, int size) {
        if (!roomRepository.existsById(roomId)) {
            throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        }

        int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(0, pageSize + 1); // 1개 더 조회해서 hasNext 판단
        List<Comment> comments = commentRepository.findByRoomIdWithCursor(roomId, cursorId, pageable);

        boolean hasNext = comments.size() > pageSize;
        if (hasNext) {
            comments = comments.subList(0, pageSize); // 초과분 제거
        }

        // 다음에 불러올 댓글 id 시작점(ex. c.id < cursorId)
        Long nextCursorId = hasNext && !comments.isEmpty()
                ? comments.get(comments.size() - 1).getId()
                : null;
        List<CommentResponse> responseLists = comments.stream()
                .map(CommentResponse::from)
                .toList();

        return CommentPageResponse.builder()
                .comments(responseLists)
                .nextCursorId(nextCursorId)
                .hasNext(hasNext)
                .build();
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long userId, Long id, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.validateOwnership(userId);
        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long userId, Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.validateOwnership(userId);
        commentRepository.deleteById(id);
    }


}
