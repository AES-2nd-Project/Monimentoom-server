package com.example.monimentoom.domain.position.repository;

import com.example.monimentoom.domain.position.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    void deleteByRoomId(Long id);

    List<Position> findByRoomId(Long id);

    @Query(value = "SELECT * FROM positions ORDER BY RAND() LIMIT :size", nativeQuery = true)
    List<Position> findRandomPositions(@Param("size") int size);
}
