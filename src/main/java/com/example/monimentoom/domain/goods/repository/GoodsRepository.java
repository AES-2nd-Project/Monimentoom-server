package com.example.monimentoom.domain.goods.repository;

import com.example.monimentoom.domain.goods.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
}
