package com.example.monimentoom.domain.room.controller;

import com.example.monimentoom.domain.room.dto.RoomCreateRequest;
import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.dto.RoomUpdateRequest;
import com.example.monimentoom.domain.room.service.RoomService;
import com.example.monimentoom.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@AuthenticationPrincipal Long userId, @Valid @RequestBody RoomCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(userId, request));
    }

    @GetMapping("/list/{nickname}")
    public ResponseEntity<List<RoomResponse>> getRoomsByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(roomService.getRoomListByNickname(nickname));
    }

    @GetMapping("/random-visit")
    public ResponseEntity<RoomResponse> getRandomRoom() {
        return ResponseEntity.ok(roomService.getRandomRoom());
    }

    @GetMapping("/{nickname}/main-room")
    public ResponseEntity<RoomResponse> visitByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.visitByNickname(11L, nickname));
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@AuthenticationPrincipal Long userId, @PathVariable Long roomId, @Valid @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(userId, roomId, request));
    }


    @DeleteMapping("/reset/{roomId}")
    public ResponseEntity<Void> resetRoom(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        roomService.resetRoom(userId, roomId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        roomService.deleteRoom(userId, roomId);
        return ResponseEntity.noContent().build();
    }
}
