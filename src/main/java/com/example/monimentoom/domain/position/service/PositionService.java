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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public PositionResponse createPosition(PositionRequest request) {
        // TODO : 추후 해당 사용자가 맞는 지 임시 검토 로직,
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

//        if (!room.getUser().getId().equals(userId)) {
//            throw new IllegalArgumentException("이 방을 수정할 권한이 없습니다.");
//        }

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
    public PositionResponse updatePosition(Long positionId, PositionRequest request) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_FOUND));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        position.update(room, request.getX(), request.getY(), request.getWallSide(), request.getWidthUnit(), request.getHeightUnit());

        // update이므로 Transactional로 save 대체 하였음.
        return PositionResponse.from(position);
    }

    /**
     * 굿즈 배치 해제
     */
    @Transactional
    public void deletePosition(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_FOUND));

        positionRepository.delete(position);
    }

}
