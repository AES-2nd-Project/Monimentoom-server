package com.example.monimentoom.domain.goods.dto;

import com.example.monimentoom.domain.goods.model.Goods;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record GoodsResponse(
        Long id,
        Long userId,
        String name,
        String description,
        String imageUrl,
        Integer price,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        List<PositionResponse> positions
) {
    /** 현재 배치되지 않음 = position : 빈 배열 */
    public static GoodsResponse from(Goods goods) {
        return of(goods, Collections.emptyList());
    }

    // position 포함 상세 조회용
    public static GoodsResponse of(Goods goods, List<PositionResponse> positions) {
        return new GoodsResponse(
                goods.getId(),
                goods.getUser().getId(),
                goods.getName(),
                goods.getDescription(),
                goods.getImageUrl(),
                goods.getPrice(),
                goods.getCreatedAt(),
                positions
        );
    }
}
