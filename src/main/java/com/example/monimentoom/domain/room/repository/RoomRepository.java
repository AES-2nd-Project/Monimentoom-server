package com.example.monimentoom.domain.room.repository;

import com.example.monimentoom.domain.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
            SELECT r FROM Room r JOIN FETCH r.user WHERE r.user.nickname = :nickname
            """)
    List<Room> findByUserNickname(String nickname);

    Long countByUserId(Long userId);

    @Query(value = "SELECT * FROM rooms WHERE id >= :randomId ORDER BY id ASC LIMIT 1",
            nativeQuery = true)
    Optional<Room> findFirstByIdGreaterThanEqual(@Param("randomId") Long randomId);

    @Query(value = "SELECT * FROM rooms ORDER BY id ASC LIMIT 1", nativeQuery = true)
    Optional<Room> findFirstRoom();

    @Query("SELECT MAX (r.id) FROM Room r")
    Long getMaxId();

    @Query("SELECT MIN (r.id) FROM Room r")
    Long getMinId();
}
