package com.example.monimentoom.domain.room.dto;

import com.example.monimentoom.domain.position.model.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowcaseItemResponse {
    private String imageUrl;
    private String goodsName;
    private String ownerNickname;

    public static ShowcaseItemResponse from(Position position) {
        return ShowcaseItemResponse.builder()
                .imageUrl(position.getGoods().getImageUrl())
                .goodsName(position.getGoods().getName())
                .ownerNickname(position.getRoom().getUser().getNickname())
                .build();
    }
}
