package com.example.monimentoom.domain.room.controller;

import com.example.monimentoom.domain.room.dto.RoomCreateRequest;
import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.dto.RoomUpdateRequest;
import com.example.monimentoom.domain.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @GetMapping("/list/{nickname}")
    public ResponseEntity<List<RoomResponse>> getRoomsByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(roomService.getRoomListByNickname(nickname));
    }

    @GetMapping("/random-visit")
    public ResponseEntity<RoomResponse> getRandomRoom() {
        return ResponseEntity.ok(roomService.getRandomRoom());
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId, @Valid @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(roomId, request));
    }


    @DeleteMapping("/reset/{roomId}")
    public ResponseEntity<Void> resetRoom(@PathVariable Long roomId) {
        roomService.resetRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
