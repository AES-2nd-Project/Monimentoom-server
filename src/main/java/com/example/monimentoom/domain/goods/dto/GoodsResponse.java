package com.example.monimentoom.domain.goods.dto;

import com.example.monimentoom.domain.goods.Goods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsResponse {
    private Long goodsId;
    private Long userId;
    private String name;
    private String description;
    private String imageUrl;
    private Number price;

    public static GoodsResponse from(Goods goods){
        return GoodsResponse.builder()
                .goodsId(goods.getId())
                .userId(goods.getUser().getId())
                .name(goods.getName())
                .description(goods.getDescription())
                .imageUrl(goods.getImageUrl())
                 .price(goods.getPrice())
                .build();
    }
}
