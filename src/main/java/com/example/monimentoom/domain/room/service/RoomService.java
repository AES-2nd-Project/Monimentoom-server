package com.example.monimentoom.domain.room.service;

import com.example.monimentoom.domain.comments.dto.CommentResponse;
import com.example.monimentoom.domain.comments.service.CommentService;
import com.example.monimentoom.domain.like.dto.LikeResponse;
import com.example.monimentoom.domain.like.service.LikeService;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.service.PositionService;
import com.example.monimentoom.domain.room.dto.*;
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
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PositionService positionService;
    private final CommentService commentService;
    private final LikeService likeService;

    @Transactional(readOnly = true)
    public List<RoomBasicResponse> getRoomListByNickname(String nickname) {
        if (!userRepository.existsByNickname(nickname)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        return roomRepository.findByUserNickname(nickname).stream()
                .map(RoomBasicResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomPositionResponse getRandomMainRoom() {
        Long maxId = userRepository.getMaxId();
        if (maxId == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        Long minId = userRepository.getMinId();
        long targetId = ThreadLocalRandom.current().nextLong(minId, maxId + 1);

        Room room = userRepository.findFirstByIdGreaterThanEqual(targetId)
                .orElseGet(() ->
                        userRepository.findFirstUser()
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                ).getMainRoom();
        if (room == null) throw new CustomException(ErrorCode.NO_MAIN_ROOM);

        List<PositionResponse> positions = positionService.getPositionsByRoomId(room.getId());
        return RoomPositionResponse.from(room, positions);
    }

    @Transactional
    public RoomBasicResponse createRoom(Long userId, RoomCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Room room = Room.builder()
                .user(user)
                .name(request.name())
                .build();
        Room saved = roomRepository.save(room);
        return RoomBasicResponse.from(saved);
    }

    @Transactional
    public RoomBasicResponse updateRoom(Long userId, Long roomId, RoomUpdateRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.validateOwnership(userId);
        room.update(request.name(), request.updateImages(), request.frameImageUrl(), request.easelImageUrl());
        return RoomBasicResponse.from(room);
    }

    @Transactional
    public void resetRoom(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.validateOwnership(userId);
        positionService.deleteAllByRoomId(roomId);
    }

    @Transactional
    public void deleteRoom(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.validateOwnership(userId);
        if (room.getUser().getMainRoom().getId().equals(roomId)) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_MAIN_ROOM);
        }
        Long roomCount = roomRepository.countByUserId(userId);
        if (roomCount <= 1) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_LAST_ROOM);
        }
        roomRepository.deleteById(roomId);
    }

    // 닉네임 방문
    @Transactional(readOnly = true)
    public RoomPositionResponse getMainRoomByNickname(String nickname) {
        Room room = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getMainRoom();

        List<PositionResponse> positions = positionService.getPositionsByRoomId(room.getId());
        return RoomPositionResponse.from(room, positions);
    }

    // 아이디로 방문(메인방 아니어도 갈 수 있게)
    @Transactional(readOnly = true)
    public RoomPositionResponse getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        List<PositionResponse> positions = positionService.getPositionsByRoomId(roomId);
        return RoomPositionResponse.from(room, positions);
    }

    @Transactional(readOnly = true)
    public RoomDetailResponse getRoomDetail(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        boolean isLoggedIn = userId != null;
        boolean isMine = isLoggedIn && userId.equals(room.getUser().getId());
        LikeResponse likeInfo = likeService.getLikeInfo(roomId, userId);
        long commentCount = commentService.getCommentCountByRoomId(roomId);
        List<CommentResponse> comments = commentService.getCommentsByRoomId(roomId);

        return RoomDetailResponse.from(room, room.getUser().getProfileImageUrl(), isLoggedIn, isMine, likeInfo.isLiked(), likeInfo.likeCount(), commentCount, comments);
    }
}
