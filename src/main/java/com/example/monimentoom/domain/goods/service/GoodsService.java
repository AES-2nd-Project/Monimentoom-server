package com.example.monimentoom.domain.goods.service;

import com.example.monimentoom.domain.goods.model.Goods;
import com.example.monimentoom.domain.goods.dto.GoodsRequest;
import com.example.monimentoom.domain.goods.dto.GoodsResponse;
import com.example.monimentoom.domain.goods.repository.GoodsRepository;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GoodsResponse createGoods(
            Long userId, GoodsRequest request
    ){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Goods goods = Goods.builder()
                .user(user)
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        return GoodsResponse.from(goodsRepository.save(goods));
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
