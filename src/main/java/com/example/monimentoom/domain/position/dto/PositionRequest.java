package com.example.monimentoom.domain.position.dto;

import com.example.monimentoom.domain.position.type.WallSide;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PositionRequest(
        @NotNull(message = "굿즈 아이디는 필수입니다.")
        Long goodsId,
        @NotNull(message = "방 아이디는 필수입니다.")
        Long roomId,
        @NotNull(message = "위치한 벽 정보는 필수입니다.")
        WallSide wallSide,
        @Min(0) @NotNull(message = "배치된 x좌표는 필수입니다.")
        Integer x,
        @Min(0) @NotNull(message = "배치된 y좌표는 필수입니다.")
        Integer y,
        @Min(0) @NotNull(message = "가로 유닛 크기는 필수입니다.")
        Integer widthUnit,
        @Min(0) @NotNull(message = "세로 유닛 크기는 필수입니다.")
        Integer heightUnit
) {}
