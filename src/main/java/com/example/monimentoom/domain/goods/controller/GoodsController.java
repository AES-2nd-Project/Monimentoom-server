package com.example.monimentoom.domain.goods.controller;

import com.example.monimentoom.domain.goods.dto.GoodsRequest;
import com.example.monimentoom.domain.goods.dto.GoodsResponse;
import com.example.monimentoom.domain.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    /** 전체 사용자의 goods 목록 조회 */
    // TODO : JWT 헤더 추출 로직 정립 전까지 임시로 path variable 사용
    @GetMapping("/{userId}")
    public ResponseEntity<List<GoodsResponse>> getGoods(
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(goodsService.getGoods(userId));
    }

    /** 사용자 인벤토리에 새로운 굿즈 추가하기 */
    // TODO : JWT 헤더 추출 로직 정립 전까지 임시로 path variable 사용
    @PostMapping("/{userId}")
    public ResponseEntity<GoodsResponse> addGoods(
            @PathVariable Long userId,
            @RequestBody GoodsRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goodsService.createGoods(userId, request));
    }

}
