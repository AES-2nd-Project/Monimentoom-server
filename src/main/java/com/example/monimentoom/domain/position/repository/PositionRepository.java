package com.example.monimentoom.domain.position.repository;

import com.example.monimentoom.domain.position.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    void deleteByRoomId(Long id);

    List<Position> findByRoomId(Long id);
}
