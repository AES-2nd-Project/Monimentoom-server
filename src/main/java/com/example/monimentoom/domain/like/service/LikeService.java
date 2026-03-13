package com.example.monimentoom.domain.like.service;

import com.example.monimentoom.domain.like.dto.LikeResponse;
import com.example.monimentoom.domain.like.model.Like;
import com.example.monimentoom.domain.like.repository.LikeRepository;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public LikeResponse getLikes(Long userId, Long roomId) {
        if (!userRepository.existsById(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        if (!roomRepository.existsById(roomId)) throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        Long likeCount = likeRepository.countByRoomId(roomId);
        Boolean isLiked = likeRepository.existsByRoomIdAndUserId(roomId, userId);
        return LikeResponse.builder()
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }

    @Transactional
    public void addLike(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        if (likeRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }
        Like like = Like.builder()
                .user(user)
                .room(room)
                .build();
        try {
            likeRepository.save(like);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

    }

    @Transactional
    public void deleteLike(Long userId, Long roomId) {
        if (!userRepository.existsById(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        if (!roomRepository.existsById(roomId)) throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        Long deleteCount = likeRepository.deleteByUserIdAndRoomId(userId, roomId);
        if (deleteCount == 0) {
            throw new CustomException(ErrorCode.ALREADY_UNLIKED);
        }
    }
}
