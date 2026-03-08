package com.example.monimentoom.domain.room.repository;

import com.example.monimentoom.domain.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
            select r from Room r join fetch r.user where r.user.nickname = :nickname
            """)
    List<Room> findByUserNickname(String nickname);

    Long countByUserId(Long userId);

    Optional<Room> findFirstByIdGreaterThanEqual(Long randomId);

    Optional<Room> findFirstByOrderByIdAsc();
}
