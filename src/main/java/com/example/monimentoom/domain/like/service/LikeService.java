package com.example.monimentoom.domain.like.service;

import com.example.monimentoom.domain.like.dto.LikeResponse;
import com.example.monimentoom.domain.like.model.Like;
import com.example.monimentoom.domain.like.repository.LikeRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public LikeResponse getLikes(Long userId, Long roomId) {
        Long likeCount = likeRepository.countByRoomId(roomId);
        Boolean isLiked = likeRepository.existsByRoomIdAndUserId(roomId, userId);
        return LikeResponse.builder()
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }

    @Transactional
    public void addLike(Long userId, Long roomId) {
        if (likeRepository.existsByRoomIdAndUserId(roomId, userId))
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        Like like = Like.builder()
                .userId(userId)
                .roomId(roomId)
                .build();
        likeRepository.save(like);
    }

    @Transactional
    public void deleteLike(Long userId, Long roomId) {
        if (!likeRepository.existsByRoomIdAndUserId(roomId, userId))
            throw new CustomException(ErrorCode.ALREADY_UNLIKED);
        likeRepository.removeByUserIdAndRoomId(userId, roomId);
    }
}
