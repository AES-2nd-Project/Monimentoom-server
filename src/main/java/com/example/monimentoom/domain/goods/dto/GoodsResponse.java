package com.example.monimentoom.domain.goods.dto;

import com.example.monimentoom.domain.goods.Goods;
import com.example.monimentoom.domain.position.Position;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    /** 중복 배치 대응 */
    private List<PositionResponse> positions;

    /** 현재 배치되지 않음 = postion : null이지만 빈 배열이 더 편할까? */
    public static GoodsResponse from(Goods goods) {
        return of(goods,null);
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
                .build();
    }
}
