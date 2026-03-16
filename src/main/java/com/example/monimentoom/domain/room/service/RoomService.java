package com.example.monimentoom.domain.room.service;

import com.example.monimentoom.domain.comments.dto.CommentResponse;
import com.example.monimentoom.domain.comments.repository.CommentRepository;
import com.example.monimentoom.domain.like.repository.LikeRepository;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.repository.PositionRepository;
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
    private final PositionRepository positionRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public List<RoomBasicResponse> getRoomListByNickname(String nickname) {
        if (!userRepository.existsByNickname(nickname)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        return roomRepository.findByUserNickname(nickname).stream()
                // 리스트는 상세한 배치정보 제외한 기본정보 제공
                .map(RoomBasicResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomPositionResponse getRandomRoom() {
        Long maxId = userRepository.getMaxId();
        if (maxId == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        Long minId = userRepository.getMinId();
        long targetId = ThreadLocalRandom.current().nextLong(minId, maxId + 1);

        // targetId보다 큰 값이 없으면( = 가장 큰 ID를 뽑았는데 이미 삭제됐다면)
        // 다시 처음(minId) 부터 찾도록 orElseGet
        Room room = userRepository.findFirstByIdGreaterThanEqual(targetId)
                .orElseGet(() ->
                        userRepository.findFirstUser()
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                ).getMainRoom();
        if (room == null) throw new CustomException(ErrorCode.NO_MAIN_ROOM);
        List<PositionResponse> positions = positionRepository.findByRoomId(room.getId()).stream()
                .map(PositionResponse::from)
                .toList();
        return RoomPositionResponse.from(room, positions);
    }

    @Transactional
    public RoomBasicResponse createRoom(Long userId, RoomCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Room room = Room.builder()
                .user(user)
                .name(request.getName())
                .build();
        Room saved = roomRepository.save(room);
        return RoomBasicResponse.from(saved);
    }

    @Transactional
    public RoomBasicResponse updateRoom(Long userId, Long roomId, RoomUpdateRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.validateOwnership(userId);
        room.update(request.getName(), request.getUpdateImages(), request.getFrameImageUrl(), request.getEaselImageUrl());
        return RoomBasicResponse.from(room);
    }

    @Transactional
    public void resetRoom(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.validateOwnership(userId);
        positionRepository.deleteByRoomId(roomId);
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

    @Transactional(readOnly = true)
    public List<ShowcaseItemResponse> getShowcaseItems(int size) {
        return positionRepository.findRandomPositions(size).stream()
                .map(ShowcaseItemResponse::from)
                .toList();
    }

    // 닉네임 방문
    @Transactional(readOnly = true)
    public RoomPositionResponse getMainRoomByNickname(String nickname) {
        Room room = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getMainRoom();
        List<PositionResponse> positions = positionRepository.findByRoomId(room.getId()).stream()
                .map(PositionResponse::from)
                .toList();

        return RoomPositionResponse.from(room, positions);
    }

    // 아이디로 방문(메인방 아니어도 갈 수 있게)
    @Transactional(readOnly = true)
    public RoomPositionResponse getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        List<PositionResponse> positions = positionRepository.findByRoomId(roomId).stream()
                .map(PositionResponse::from)
                .toList();

        return RoomPositionResponse.from(room, positions);
    }

    @Transactional(readOnly = true)
    public RoomDetailResponse getRoomDetail(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        boolean isLoggedIn = userId != null;
        boolean isMine = isLoggedIn && userId.equals(room.getUser().getId());
        boolean isLiked = isLoggedIn && likeRepository.existsByRoomIdAndUserId(roomId, userId);
        long likeCount = likeRepository.countByRoomId(roomId);
        long commentCount = commentRepository.countByRoomId(roomId);
        List<CommentResponse> comments = commentRepository.findByRoomIdWithUser(roomId).stream()
                .map(CommentResponse::from)
                .toList();
        return RoomDetailResponse.from(room, room.getUser().getProfileImageUrl(), isLoggedIn, isMine, isLiked, likeCount, commentCount, comments);
    }
}
