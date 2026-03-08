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

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final Random random = new Random();

    public List<Room> getRoomListByNickname(String nickname) {
        return roomRepository.findByUserNickname(nickname);
    }

    public Room getRandomRoom() {
        long count = roomRepository.count();
        int randomIndex = random.nextInt((int) count);
        // randomIndex 개의 page 중에서 한개만 가져온다
        // LIMIT 1 OFFSET randomIndex(randomIndex 개만큼 건너뛰고 한개 가져와라)와 같은 의미
        PageRequest page = PageRequest.of(randomIndex, 1);
        return roomRepository.findAll(page)
                .getContent()
                // 첫 번째 요소 꺼내기
                .get(0);
    }

    public RoomResponse createRoom(RoomRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Room room = Room.builder()
                .user(user)
                .roomName(request.getRoomName())
                .build();
        roomRepository.save(room);
        return RoomResponse.builder()
                .id(room.getId())
                .userId(room.getUser().getId())
                .roomName(room.getRoomName())
                .build();
    }

    public void resetRoom(long room_id) {
        positionRepository.deleteByRoomId(room_id);
    }

    // TODO: room 1개일땐 삭제못하게 해야할지? -> 0개일때 랜덤방문도 어떻게 할지 생각 필요
    public void deleteRoom(long room_id) {
        roomRepository.deleteById(room_id);
    }
    // 후순위
//   comment room에 맞는 댓글 가져오기 이건 comment에 넣는게 맞을듯
    // rooms/setPrimary -> primary column 있어야할듯

}
