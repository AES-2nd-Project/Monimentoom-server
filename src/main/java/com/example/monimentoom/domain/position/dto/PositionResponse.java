package com.example.monimentoom.domain.position.dto;

import com.example.monimentoom.domain.position.model.Position;
import com.example.monimentoom.domain.position.type.WallSide;

public record PositionResponse(
        Long id,
        Long goodsId,
        Long roomId,
        WallSide wallSide,
        Integer x,
        Integer y,
        Integer widthUnit,
        Integer heightUnit,
        String imageUrl,
     String goodsName,
                 String goodsDescription
) {
    public static PositionResponse from(Position position) {
        if (position == null) return null;
        return new PositionResponse(
                position.getId(),
                position.getGoods().getId(),
                position.getRoom().getId(),
                position.getWall(),
                position.getX(),
                position.getY(),
                position.getWidthUnit(),
                position.getHeightUnit(),
                position.getGoods().getImageUrl(),
                position.getGoods().getName(),
                position.getGoods().getDescription()
        );
    }
}
