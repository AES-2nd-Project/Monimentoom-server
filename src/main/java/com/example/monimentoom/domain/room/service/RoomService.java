package com.example.monimentoom.domain.room.service;

import com.example.monimentoom.domain.comments.dto.CommentResponse;
import com.example.monimentoom.domain.comments.repository.CommentRepository;
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

    // 로그인하지 않은 사용자, 소유주가 아닌 사용자도 닉네임으로 방 목록 조회 가능하도록 사용자 검증 없음.
    public List<RoomBasicResponse> getRoomListByNickname(String nickname) {
        if (!userRepository.existsByNickname(nickname)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        return roomRepository.findByUserNickname(nickname).stream()
                // 리스트는 상세한 배치정보 제외한 기본정보 제공
                .map(RoomBasicResponse::from)
                .toList();
    }

    // 로그인하지 않은 사용자, 소유주가 아닌 사용자도 방 조회 가능하도록 사용자 검증 없음.
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
        room.setName(request.getName());
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
        Long roomCount = roomRepository.countByUserId(userId);
        if (roomCount <= 1) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_LAST_ROOM);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (user.getMainRoom().getId().equals(roomId)) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_MAIN_ROOM);
        }
        roomRepository.deleteById(roomId);
    }

    // 닉네임 방문
    @Transactional(readOnly = true)
    public RoomPositionResponse getMainRoomByNickname(Long userId, String nickname) {
        if (!userRepository.existsById(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        Room room = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getMainRoom();
        List<PositionResponse> positions = positionRepository.findByRoomId(room.getId()).stream()
                .map(PositionResponse::from)
                .toList();

        return RoomPositionResponse.from(room, positions);
    }

    @Transactional(readOnly = true)
    public RoomDetailResponse getRoomDetail(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<CommentResponse> comments = commentRepository.findByRoomIdWithUser(roomId).stream()
                .map(CommentResponse::from)
                .toList();
        return RoomDetailResponse.from(room, user.getProfileImageUrl(), userId.equals(room.getUser().getId()), comments);
    }
}
