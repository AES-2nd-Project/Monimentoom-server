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

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final Random random = new Random();

    public List<RoomResponse> getRoomListByNickname(String nickname) {
        return roomRepository.findByUserNickname(nickname).stream()
                .map(RoomResponse::from)
                .toList();
    }

    public RoomResponse getRandomRoom() {
        long count = roomRepository.count();
        if (count == 0) {
            throw new IllegalArgumentException("방이 존재하지 않습니다.");
        }
        int randomIndex = random.nextInt((int) count);
        // randomIndex 개의 page 중에서 한개만 가져온다
        // LIMIT 1 OFFSET randomIndex(randomIndex 개만큼 건너뛰고 한개 가져와라)와 같은 의미
        PageRequest page = PageRequest.of(randomIndex, 1);
        List<Room> rooms = roomRepository.findAll(page).getContent();
        if (rooms.isEmpty()) {
            throw new IllegalArgumentException("방 목록이 비었습니다. 다시 시도해주세요.");
        }
        return RoomResponse.from(
                roomRepository.findAll(page)
                        .getContent()
                        // 첫 번째 요소 꺼내기
                        .get(0)
        );
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
