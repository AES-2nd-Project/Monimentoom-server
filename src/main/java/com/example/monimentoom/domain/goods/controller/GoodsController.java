package com.example.monimentoom.domain.goods.controller;

import com.example.monimentoom.domain.goods.dto.GoodsRequest;
import com.example.monimentoom.domain.goods.dto.GoodsResponse;
import com.example.monimentoom.domain.goods.service.GoodsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    /** 특정 사용자의 goods 전체 목록 조회 */
    @GetMapping
    public ResponseEntity<List<GoodsResponse>> getGoods(
            @AuthenticationPrincipal Long userId
    ){
        return ResponseEntity.ok(goodsService.getGoods(userId));
    }

    /** 사용자 인벤토리에 새로운 굿즈 추가하기 */
    @PostMapping
    public ResponseEntity<GoodsResponse> addGoods(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody GoodsRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goodsService.createGoods(userId, request));
    }

    /** 굿즈 상세 정보 수정 (이름, 가격,  */
    @PatchMapping
    public ResponseEntity<GoodsResponse> updateGoods(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long goodsId,
            @Valid @RequestBody GoodsRequest request
    ){
        return ResponseEntity.ok(goodsService.updateGoods(userId, goodsId, request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteGoods(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long goodsId
    ){
        goodsService.deleteGoods(userId, goodsId);
        return ResponseEntity.noContent().build();
    }
}
