package com.example.monimentoom.domain.goods.service;

import com.example.monimentoom.domain.goods.Goods;
import com.example.monimentoom.domain.goods.dto.GoodsRequest;
import com.example.monimentoom.domain.goods.dto.GoodsResponse;
import com.example.monimentoom.domain.goods.repository.GoodsRepository;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.repository.PositionRepository;
import com.example.monimentoom.domain.user.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    @Transactional
    public GoodsResponse createGoods(
            Long userId, GoodsRequest request
    ){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 테스트용
        //  User user = userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Goods goods = Goods.builder()
                .user(user)
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        // 처음 추가 시에는 postion을 null로 두어 미배치 상태로 설정.
        return GoodsResponse.of(goodsRepository.save(goods), null);
    }

    public List<GoodsResponse> getGoods(Long userId) {
        List<Goods> goodsList = goodsRepository.findAllByUserIdWithPositions(userId);
        return goodsList.stream()
                .map(goods -> {
                    List<PositionResponse> positionDtoList = goods.getPositions().stream().map(PositionResponse::from).toList();
                    return GoodsResponse.of(goods, positionDtoList);
                })
                .toList();
    }

}
