package com.example.monimentoom.domain.position.repository;

import com.example.monimentoom.domain.position.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
