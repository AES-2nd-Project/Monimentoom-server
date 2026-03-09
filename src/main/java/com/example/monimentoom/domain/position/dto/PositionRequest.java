package com.example.monimentoom.domain.position.dto;

import com.example.monimentoom.domain.position.type.WallSide;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PositionRequest {
    @NotNull(message = "굿즈 아이디는 필수입니다.")
    private Long goodsId;
    @NotNull(message = "방 아이디는 필수입니다.")
    private Long roomId;
    @NotNull(message = "위치한 벽 정보는 필수입니다.")
    private WallSide wallSide;
    @NotNull(message = "배치된 x좌표는 필수입니다.")
    private Integer x;
    @NotNull(message = "배치된 y좌표는 필수입니다.")
    private Integer y;
    @NotNull(message = "가로 유닛 크기는 필수입니다.")
    private Integer widthUnit;
    @NotNull(message = "세로 유닛 크기는 필수입니다.")
    private Integer heightUnit;
}
