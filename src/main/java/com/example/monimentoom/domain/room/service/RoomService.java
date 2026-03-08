package com.example.monimentoom.domain.room.service;

import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.room.dto.RoomRequest;
import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
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
        long count = roomRepository.count();
        if (count == 0) throw new IllegalArgumentException("방이 존재하지 않습니다.");
        // ID 기반 랜덤 샘플링
        Long randomId = ThreadLocalRandom.current().nextLong(1, count + 1);
        // 해당 ID보다 크거나 같은 첫 번째 데이터를 가져옴
        Room room = roomRepository.findFirstByIdGreaterThanEqual(randomId)
                .orElseGet(() ->
                        // 만약 마지막 ID 보다 큰 값이 나왔다면 다시 첫 번째 데이터를 반환
                        roomRepository.findFirstByOrderByIdAsc()
                                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."))
                );
        return RoomResponse.from(room);
    }

    public RoomResponse createRoom(RoomRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Room room = Room.builder()
                .user(user)
                .roomName(request.getRoomName())
                .build();
        Room saved = roomRepository.save(room);
        return RoomResponse.from(saved);
    }

    @Transactional
    public void resetRoom(long roomId) {
        positionRepository.deleteByRoomId(roomId);
    }

    @Transactional
    public void deleteRoom(long roomId) {
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
