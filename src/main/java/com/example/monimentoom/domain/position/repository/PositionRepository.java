package com.example.monimentoom.domain.position.repository;

import com.example.monimentoom.domain.position.model.Position;
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

    @Query(value = "SELECT * FROM positions WHERE id >= :startId ORDER BY id ASC LIMIT :size", nativeQuery = true)
    List<Position> findPositionsFromId(@Param("startId") long startId, @Param("size") int size);
}
