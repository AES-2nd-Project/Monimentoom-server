package com.example.monimentoom.domain.position.service;

import com.example.monimentoom.domain.position.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
}
