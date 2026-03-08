package com.example.monimentoom.domain.position.dto;

import com.example.monimentoom.domain.position.Position;
import com.example.monimentoom.domain.position.WallSide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionResponse {
    private Long id;
    private Long goodsId;
    private Long roomId;
    private WallSide wallSide;
    private Integer x;
    private Integer y;
    private Integer widthUnit;
    private Integer heightUnit;

    public static PositionResponse from(Position position) {
        if (position == null) return null;

        return PositionResponse.builder()
                .id(position.getId())
                .roomId(position.getRoom().getId())
                .goodsId(position.getGoods().getId())
                .wallSide(position.getWall())
                .x(position.getX())
                .y(position.getY())
                .widthUnit(position.getWidthUnit())
                .heightUnit(position.getHeightUnit())
                .build();
    }
}
