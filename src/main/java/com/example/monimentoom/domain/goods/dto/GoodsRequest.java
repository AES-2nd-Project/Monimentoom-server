package com.example.monimentoom.domain.goods.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoodsRequest {
    @NotBlank(message = "굿즈 이름은 필수입니다.")
    private String name;
    private String description;
    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String imageUrl;
    private Integer price;
}
