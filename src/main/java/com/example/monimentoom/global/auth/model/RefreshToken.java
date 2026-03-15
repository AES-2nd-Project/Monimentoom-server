package com.example.monimentoom.global.auth.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor

public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 512)
    private String token;

    @Column(nullable = false, length = 100)
    private String deviceId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Builder
    private RefreshToken(Long userId, String token, String deviceId, LocalDateTime expiresAt) {
        this.userId    = userId;
        this.token     = token;
        this.deviceId  = deviceId;
        this.expiresAt = expiresAt;
        this.revoked   = false;
        this.createdAt = LocalDateTime.now();
    }

    public void revoke() { this.revoked = true; }
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }
    public boolean isValid()   { return !revoked && !isExpired(); }
}
