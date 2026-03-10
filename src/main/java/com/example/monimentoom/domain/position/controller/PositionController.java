package com.example.monimentoom.domain.position.controller;

import com.example.monimentoom.domain.position.dto.PositionRequest;
import com.example.monimentoom.domain.position.dto.PositionResponse;
import com.example.monimentoom.domain.position.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/position")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @PostMapping
    public ResponseEntity<PositionResponse> addPosition(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PositionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(positionService.createPosition(userId, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PositionResponse> updatePosition(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody PositionRequest request
    ){
        return ResponseEntity.ok(positionService.updatePosition(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id){
        positionService.deletePosition(userId, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
