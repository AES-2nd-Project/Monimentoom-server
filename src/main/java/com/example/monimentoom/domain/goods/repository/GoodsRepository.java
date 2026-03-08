package com.example.monimentoom.domain.goods.repository;

import com.example.monimentoom.domain.goods.model.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    List<Goods> findAllByUserId(Long userId);

    // 굿즈와 포지션을 한 번에 조인해서 가져오도록
    @Query("SELECT DISTINCT g FROM Goods g " +
            "LEFT JOIN FETCH g.positions " +
            "WHERE g.user.id = :userId")
    List<Goods> findAllByUserIdWithPositions(@Param("userId") Long userId);
}
