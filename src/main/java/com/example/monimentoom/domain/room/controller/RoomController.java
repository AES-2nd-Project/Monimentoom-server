package com.example.monimentoom.domain.room.controller;

import com.example.monimentoom.domain.room.dto.*;
import com.example.monimentoom.domain.room.service.RoomService;
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

    // 방 생성
    @PostMapping
    public ResponseEntity<RoomBasicResponse> createRoom(@AuthenticationPrincipal Long userId, @Valid @RequestBody RoomCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(userId, request));
    }

    // 랜덤 방 가져오기(인증 없어도 조회 가능)
    @GetMapping("/random")
    public ResponseEntity<RoomPositionResponse> getRandomRoom() {
        return ResponseEntity.ok(roomService.getRandomMainRoom());
    }

    // 배치된 굿즈 랜덤 쇼케이스(인증 없어도 조회 가능)
    @GetMapping("/showcase")
    public ResponseEntity<List<ShowcaseItemResponse>> getShowcase(@RequestParam(defaultValue = "20") int size) {
        int clampedSize = Math.max(1, Math.min(size, 50));
        return ResponseEntity.ok(roomService.getShowcaseItems(clampedSize));
    }

    // 닉네임으로 방 리스트 가져오기(인증 없어도 조회 가능)
    @GetMapping
    public ResponseEntity<List<RoomBasicResponse>> getRoomsByNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(roomService.getRoomListByNickname(nickname));
    }

    // 닉네임의 메인 방 가져오기(인증 없어도 조회 가능)
    @GetMapping("/{nickname}/main")
    public ResponseEntity<RoomPositionResponse> getMainRoomByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(roomService.getMainRoomByNickname(nickname));
    }

    // 메인 방 외에도 아이디로 조회(인증 없어도 조회 가능)
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomPositionResponse> getRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }

    // 방 상세 조회(주인정보 + 댓글)(인증 없어도 조회 가능)
    @GetMapping("/{roomId}/detail")
    public ResponseEntity<RoomDetailResponse> getRoomDetail(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        return ResponseEntity.ok((roomService.getRoomDetail(userId, roomId)));
    }

    // 방 정보 수정
    @PatchMapping("/{roomId}")
    public ResponseEntity<RoomBasicResponse> updateRoom(@AuthenticationPrincipal Long userId, @PathVariable Long roomId, @Valid @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(userId, roomId, request));
    }


    // 방 배치 초기화
    @DeleteMapping("/{roomId}/positions")
    public ResponseEntity<Void> resetRoom(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        roomService.resetRoom(userId, roomId);
        return ResponseEntity.noContent().build();
    }

    // 방 삭제
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        roomService.deleteRoom(userId, roomId);
        return ResponseEntity.noContent().build();
    }
}
