package com.example.monimentoom.domain.goods.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoodsRequest {
    private String name;
    private String description;
    private String imageUrl;
    private Integer price;
}
