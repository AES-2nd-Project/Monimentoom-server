package com.example.monimentoom.domain.position.service;

import com.example.monimentoom.domain.goods.Goods;
import com.example.monimentoom.domain.goods.dto.GoodsResponse;
import com.example.monimentoom.domain.goods.repository.GoodsRepository;
import com.example.monimentoom.domain.position.Position;
import com.example.monimentoom.domain.position.dto.PositionRequest;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.room.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final GoodsRepository goodsRepository;
    private final RoomRepository roomRepository;

    /**
     * 굿즈 새로 배치
     */
    @Transactional
    public PositionResponse createPosition(Long userId, PositionRequest request) {
        // TODO : 추후 해당 사용자가 맞는 지 임시 검토 로직,
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        if (!room.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("이 방을 수정할 권한이 없습니다.");
        }

        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 굿즈입니다."));
        Position position = Position.builder()
                .room(room)
                .goods(goods)
                .x(request.getX())
                .y(request.getY())
                .wall(request.getWallSide())
                .widthUnit(request.getWidthUnit())
                .heightUnit(request.getHeightUnit())
                .build();

        return PositionResponse.from(positionRepository.save(position));
    }

    /**
     * 굿즈 위치, 크기 변경 - 처음 놓는 거랑, 변경 시키는 거랑 분리하는게 정말 더 좋은가?
     */
    @Transactional
    public PositionResponse patchPosition(Long userId, Long positionId, PositionRequest request) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 위치 정보입니다."));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 굿즈입니다."));

        position.update(room, request.getX(), request.getY(), request.getWallSide(), request.getWidthUnit(), request.getHeightUnit());

        // update이므로 Transactional로 save 대체 하였음.
        return PositionResponse.from(position);
    }

    /**
     * 굿즈 배치 해제
     */
    @Transactional
    public void deletePosition(Long userId, Long positionId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 위치 정보입니다."));

        positionRepository.delete(position);
    }

}
