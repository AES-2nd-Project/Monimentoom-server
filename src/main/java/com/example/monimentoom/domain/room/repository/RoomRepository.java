package com.example.monimentoom.domain.room.repository;

import com.example.monimentoom.domain.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByUserNickname(String nickname);
}
