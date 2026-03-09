package com.example.monimentoom.domain.room.service;

import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.room.dto.RoomCreateRequest;
import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.dto.RoomUpdateRequest;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.User;
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
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Room room = Room.builder()
                .user(user)
                .name(request.getName())
                .build();
        Room saved = roomRepository.save(room);
        return RoomResponse.from(saved);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        room.setName(request.getName());
        return RoomResponse.from(room);
    }

    @Transactional
    public void resetRoom(Long roomId) {
        positionRepository.deleteByRoomId(roomId);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        Long userId = room.getUser().getId();
        Long roomCount = roomRepository.countByUserId(userId);
        if (roomCount <= 1) {
            throw new IllegalArgumentException("방은 최소 1개 있어야 합니다.");
        }
        roomRepository.deleteById(roomId);
    }

}
