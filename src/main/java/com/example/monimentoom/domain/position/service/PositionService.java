package com.example.monimentoom.domain.position.service;

import com.example.monimentoom.domain.goods.model.Goods;
import com.example.monimentoom.domain.goods.repository.GoodsRepository;
import com.example.monimentoom.domain.position.model.Position;
import com.example.monimentoom.domain.position.dto.PositionRequest;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private final GoodsRepository goodsRepository;
    private final RoomRepository roomRepository;

    /**
     * 굿즈 새로 배치
     */
    @Transactional
    public PositionResponse createPosition(Long userId, PositionRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        room.validateOwnership(userId);

        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new CustomException(ErrorCode.GOODS_NOT_FOUND));
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

    @Transactional
    public PositionResponse updatePosition(Long userId, Long positionId, PositionRequest request) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_FOUND));
        // 기존 방도 사용자 것이 맞는지 검증
        Room currentRoom = position.getRoom();
        currentRoom.validateOwnership(userId);

        // 방 이동이 없다면, DB 조회 생략하여 성능 향상
        Room targetRoom;
        if (currentRoom.getId().equals(request.getRoomId())) {
            targetRoom = currentRoom;
        } else {
            targetRoom = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
            targetRoom.validateOwnership(userId);
        }
        position.update(targetRoom, request.getX(), request.getY(), request.getWallSide(), request.getWidthUnit(), request.getHeightUnit());
        // update이므로 Transactional로 save 대체 하였음.
        return PositionResponse.from(position);
    }

    /**
     * 굿즈 배치 해제
     */
    @Transactional
    public void deletePosition(Long userId, Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_FOUND));
        Room room = position.getRoom();
        room.validateOwnership(userId);

        positionRepository.delete(position);
    }

}
