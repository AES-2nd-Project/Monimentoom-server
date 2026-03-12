package com.example.monimentoom.domain.goods.dto;

import com.example.monimentoom.domain.goods.model.Goods;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsResponse {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String imageUrl;
    private Integer price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    /** 중복 배치 대응 */
    private List<PositionResponse> positions;

    /** 현재 배치되지 않음 = position : 빈 배열 */
    public static GoodsResponse from(Goods goods) {
        return of(goods, Collections.emptyList());
    }

    // position 포함 상세 조회용
    public static GoodsResponse of(Goods goods, List<PositionResponse> positions) {
        return GoodsResponse.builder()
                .id(goods.getId())
                .userId(goods.getUser().getId())
                .name(goods.getName())
                .description(goods.getDescription())
                .imageUrl(goods.getImageUrl())
                .price(goods.getPrice())
                .positions(positions)
                .createdAt(goods.getCreatedAt())
                .build();
    }
}
