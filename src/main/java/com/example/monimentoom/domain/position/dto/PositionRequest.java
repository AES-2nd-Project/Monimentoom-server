package com.example.monimentoom.domain.position.dto;

import com.example.monimentoom.domain.position.type.WallSide;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PositionRequest {
    private Long goodsId;
    private Long roomId;
    private WallSide wallSide;
    private Integer x;
    private Integer y;
    private Integer widthUnit;
    private Integer heightUnit;
}
