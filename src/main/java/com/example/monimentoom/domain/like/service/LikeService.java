package com.example.monimentoom.domain.like.service;

import com.example.monimentoom.domain.like.dto.LikeResponse;
import com.example.monimentoom.domain.like.model.Like;
import com.example.monimentoom.domain.like.repository.LikeRepository;
import com.example.monimentoom.domain.room.dto.RoomBasicResponse;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    /**
     * 좋아요 정보 조회 (비로그인 허용)
     * userId가 null -> isLiked = false로 반환
     */
    @Transactional(readOnly = true)
    public LikeResponse getLikeInfo(Long roomId, Long userId) {
        long likeCount = likeRepository.countByRoomId(roomId);
        boolean isLiked = userId != null && likeRepository.existsByRoomIdAndUserId(roomId, userId);
        return new LikeResponse(likeCount, isLiked);
    }

    /**
     * 좋아요 정보 조회 (로그인 필수 — LikeController용)
     */
    @Transactional(readOnly = true)
    public LikeResponse getLikes(Long userId, Long roomId) {
        if (!userRepository.existsById(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        if (!roomRepository.existsById(roomId)) throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        return getLikeInfo(roomId, userId);
    }

    @Transactional(readOnly = true)
    public List<RoomBasicResponse> getLikedRooms(Long userId) {
        if (!userRepository.existsById(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        return likeRepository.findByUserId(userId)
                .stream()
                .map(like -> RoomBasicResponse.from(like.getRoom()))
                .toList();
    }

    @Transactional
    public LikeResponse addLike(Long userId, Long roomId) {
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
        long likeCount = likeRepository.countByRoomId(roomId);
        return new LikeResponse(likeCount, true);
    }

    @Transactional
    public LikeResponse deleteLike(Long userId, Long roomId) {
        if (!userRepository.existsById(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        if (!roomRepository.existsById(roomId)) throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        long deleteCount = likeRepository.deleteByUserIdAndRoomId(userId, roomId);
        if (deleteCount == 0) {
            throw new CustomException(ErrorCode.ALREADY_UNLIKED);
        }
        long likeCount = likeRepository.countByRoomId(roomId);
        return new LikeResponse(likeCount, false);
    }
}
