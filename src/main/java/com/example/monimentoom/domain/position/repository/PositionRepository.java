package com.example.monimentoom.domain.position.repository;

import com.example.monimentoom.domain.position.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
