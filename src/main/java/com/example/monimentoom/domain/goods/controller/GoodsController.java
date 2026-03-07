package com.example.monimentoom.domain.goods.controller;

import com.example.monimentoom.domain.goods.Goods;
import com.example.monimentoom.domain.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping
    public Goods getGoods(){
//        return goodsService.
    }

}
