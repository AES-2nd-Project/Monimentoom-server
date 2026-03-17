package com.example.monimentoom.domain.position.service;

import com.example.monimentoom.domain.goods.model.Goods;
import com.example.monimentoom.domain.goods.repository.GoodsRepository;
import com.example.monimentoom.domain.position.model.Position;
import com.example.monimentoom.domain.position.dto.PositionRequest;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.room.dto.ShowcaseItemResponse;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private final GoodsRepository goodsRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<ShowcaseItemResponse> getShowcaseItems(int size) {
        if (size <= 0) return List.of();

        Long maxId = positionRepository.getMaxId();
        if (maxId == null) return List.of();

        Long minId = positionRepository.getMinId();
        long startId = ThreadLocalRandom.current().nextLong(minId, maxId + 1);

        // 중복 제거를 감안해 넉넉하게 조회
        int fetchSize = size * 3;

        List<Position> result = new ArrayList<>(positionRepository.findPositionsFromId(startId, PageRequest.of(0, fetchSize)));
        if (result.size() < fetchSize) {
            Set<Long> foundIds = new HashSet<>(result.size());
            for (Position position : result) {
                foundIds.add(position.getId());
            }
            positionRepository.findPositionsFromId(minId, PageRequest.of(0, fetchSize)).stream()
                    .filter(p -> !foundIds.contains(p.getId())) // 이미 가져온건 예외처리
                    .forEach(result::add);
        }

        // goods ID 기준으로 중복 제거 후 size만큼 반환
        Set<Long> seenGoodsIds = new LinkedHashSet<>();
        return result.stream()
                // 처음 추가한 것만 추가됨
                .filter(p -> seenGoodsIds.add(p.getGoods().getId()))
                .limit(size)
                .map(ShowcaseItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PositionResponse> getPositionsByRoomId(Long roomId) {
        return positionRepository.findByRoomId(roomId).stream()
                .map(PositionResponse::from)
                .toList();
    }

    /**
     * 방의 배치를 전부 초기화할 때 사용
     */
    @Transactional
    public void deleteAllByRoomId(Long roomId) {
        positionRepository.deleteByRoomId(roomId);
    }

    @Transactional
    public PositionResponse createPosition(Long userId, PositionRequest request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        room.validateOwnership(userId);

        Goods goods = goodsRepository.findById(request.goodsId())
                .orElseThrow(() -> new CustomException(ErrorCode.GOODS_NOT_FOUND));
        goods.validateOwnership(userId);
        Position position = Position.builder()
                .room(room)
                .goods(goods)
                .x(request.x())
                .y(request.y())
                .wall(request.wallSide())
                .widthUnit(request.widthUnit())
                .heightUnit(request.heightUnit())
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
        if (currentRoom.getId().equals(request.roomId())) {
            targetRoom = currentRoom;
        } else {
            targetRoom = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
            targetRoom.validateOwnership(userId);
        }
        position.update(targetRoom, request.x(), request.y(), request.wallSide(), request.widthUnit(), request.heightUnit());
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
