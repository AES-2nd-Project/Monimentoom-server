package com.example.monimentoom.domain.room.service;

import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.room.dto.RoomCreateRequest;
import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.dto.RoomUpdateRequest;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
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

    public List<RoomResponse> getRoomListByNickname(String nickname) {
        return roomRepository.findByUserNickname(nickname).stream()
                .map(RoomResponse::from)
                .toList();
    }

    public RoomResponse getRandomRoom() {
        Long maxId = roomRepository.getMaxId();
        if (maxId == null) throw new IllegalArgumentException("방을 찾을 수 없습니다.");

        Long minId = roomRepository.getMinId();
        long targetId = ThreadLocalRandom.current().nextLong(minId, maxId + 1);

        // targetId보다 큰 값이 없으면( = 가장 큰 ID를 뽑았는데 이미 삭제됐다면)
        // 다시 처음(minId) 부터 찾도록 orElseGet
        Room room = roomRepository.findFirstByIdGreaterThanEqual(targetId)
                .orElseGet(() ->
                        roomRepository.findFirstRoom()
                                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."))
                );
        return RoomResponse.from(room);
    }

    @Transactional
    public RoomResponse createRoom(RoomCreateRequest request) {
        // TODO: request의 userId 대신 현재 로그인한 유저아이디로 가져오도록
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Room room = Room.builder()
                .user(user)
                .name(request.getName())
                .build();
        Room saved = roomRepository.save(room);
        return RoomResponse.from(saved);
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomUpdateRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));
        // TODO: 로그인 사용자와 방 소유자 일치 여부 검증
        room.setName(request.getName());
        return RoomResponse.from(room);
    }

    @Transactional
    public void resetRoom(Long roomId) {
        // TODO: 로그인 사용자와 방 소유자 일치 여부 검증(findById -> findByIdAndUserId로 변경 검토)
        positionRepository.deleteByRoomId(roomId);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        // TODO: 로그인 사용자와 방 소유자 일치 여부 검증
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));
        Long userId = room.getUser().getId();
        Long roomCount = roomRepository.countByUserId(userId);
        if (roomCount <= 1) {
            throw new IllegalArgumentException("방은 최소 1개 있어야 합니다.");
        }
        roomRepository.deleteById(roomId);
    }

}
