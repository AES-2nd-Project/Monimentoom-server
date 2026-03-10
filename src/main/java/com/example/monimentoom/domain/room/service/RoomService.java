package com.example.monimentoom.domain.room.service;

import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.room.dto.RoomCreateRequest;
import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.dto.RoomUpdateRequest;
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

    // 로그인하지 않은 사용자, 소유주가 아닌 사용자도 닉네임으로 방 목록 조회 가능하도록 사용자 검증 없음.
    public List<RoomResponse> getRoomListByNickname(String nickname) {
        if (!userRepository.existsByNickname(nickname)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        return roomRepository.findByUserNickname(nickname).stream()
                .map(RoomResponse::from)
                .toList();
    }

    // 로그인하지 않은 사용자, 소유주가 아닌 사용자도 방 조회 가능하도록 사용자 검증 없음.
    public RoomResponse getRandomRoom() {
        Long maxId = userRepository.getMaxId();
        if (maxId == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        Long minId = userRepository.getMinId();
        long targetId = ThreadLocalRandom.current().nextLong(minId, maxId + 1);

        // targetId보다 큰 값이 없으면( = 가장 큰 ID를 뽑았는데 이미 삭제됐다면)
        // 다시 처음(minId) 부터 찾도록 orElseGet
        // TODO: 내 방은 제외해야 함
        Room room = userRepository.findFirstByIdGreaterThanEqual(targetId)
                .orElseGet(() ->
                        userRepository.findFirstUser()
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                ).getMainRoom();
        return RoomResponse.from(room);
    }

    @Transactional
    public RoomResponse createRoom(Long userId, RoomCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Room room = Room.builder()
                .user(user)
                .name(request.getName())
                .build();
        Room saved = roomRepository.save(room);
        return RoomResponse.from(saved);
    }

    @Transactional
    public RoomResponse updateRoom(Long userId, Long roomId, RoomUpdateRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.validateOwnership(userId);
        room.setName(request.getName());
        return RoomResponse.from(room);
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
        roomRepository.deleteById(roomId);
    }

    // 닉네임 방문
    @Transactional(readOnly = true)
    public RoomResponse getMainRoomByNickname(Long userId, String nickname) {
        if (!userRepository.existsById(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        Room room = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getMainRoom();
//        내 방인지 일치하게 컬럼 넣어줘야함 response에...
        return RoomResponse.from(room);
    }

}
