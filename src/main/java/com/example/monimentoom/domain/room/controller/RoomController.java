package com.example.monimentoom.domain.room.controller;

import com.example.monimentoom.domain.room.dto.RoomRequest;
import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.service.RoomService;
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

    @GetMapping("/{nickname}")
    public ResponseEntity<List<Room>> getRoomsByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(roomService.getRoomListByNickname(nickname));
    }

    @GetMapping("/random-visit")
    public ResponseEntity<Room> getRandomRoom() {
        return ResponseEntity.ok(roomService.getRandomRoom());
    }

    @PostMapping("/create")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @DeleteMapping("/reset/{roomId}")
    public ResponseEntity<Void> resetRoom(@PathVariable Long roomId) {
        roomService.resetRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
