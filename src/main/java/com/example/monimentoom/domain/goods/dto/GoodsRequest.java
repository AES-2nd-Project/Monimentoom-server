package com.example.monimentoom.domain.goods.dto;

import jakarta.validation.constraints.NotBlank;

public record GoodsRequest(
        @NotBlank(message = "굿즈 이름은 필수입니다.")
        String name,
        String description,
        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,
        Integer price
) {}
