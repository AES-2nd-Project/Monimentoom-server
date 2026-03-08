package com.example.monimentoom.domain.position.controller;

import com.example.monimentoom.domain.position.dto.PositionRequest;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/position")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @PostMapping("/{id}")
    public ResponseEntity<PositionResponse> addPosition(
            @PathVariable Long userId,
            @Valid @RequestBody PositionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(positionService.createPosition(userId, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PositionResponse> updatePosition(
            @PathVariable Long userId,
            @PathVariable Long id,
            @Valid @RequestBody PositionRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(positionService.updatePosition(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id, @PathVariable Long userId){
        positionService.deletePosition(userId, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
