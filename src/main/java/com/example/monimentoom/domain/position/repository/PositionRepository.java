package com.example.monimentoom.domain.position.repository;

import com.example.monimentoom.domain.position.model.Position;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    void deleteByRoomId(Long id);

    List<Position> findByRoomId(Long id);

    @Query("SELECT MAX(p.id) FROM Position p")
    Long getMaxId();

    @Query("SELECT MIN(p.id) FROM Position p")
    Long getMinId();

    @Query("SELECT p FROM Position p JOIN FETCH p.goods JOIN FETCH p.room r JOIN FETCH r.user WHERE p.id >= :startId ORDER BY p.id ASC")
    List<Position> findPositionsFromId(@Param("startId") long startId, Pageable pageable);
}
