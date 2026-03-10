package com.example.monimentoom.domain.user.repository;

import com.example.monimentoom.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Query(value = "SELECT * FROM users WHERE id >= :randomId ORDER BY id ASC LIMIT 1",
            nativeQuery = true)
    Optional<User> findFirstByIdGreaterThanEqual(@Param("randomId") Long randomId);

    @Query(value = "SELECT * FROM users ORDER BY id ASC LIMIT 1", nativeQuery = true)
    Optional<User> findFirstUser();

    @Query("SELECT MAX (u.id) FROM User u")
    Long getMaxId();

    @Query("SELECT MIN (u.id) FROM User u")
    Long getMinId();

    Optional<User> findByNickname(String nickname);
}
