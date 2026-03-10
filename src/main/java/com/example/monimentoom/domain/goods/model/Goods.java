package com.example.monimentoom.domain.goods.model;

import com.example.monimentoom.domain.position.model.Position;
import com.example.monimentoom.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    private String description;
    private Integer price;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Position> positions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Goods(User user, String name, String imageUrl, String description, Integer price, List<Position> positions) {
        this.user = user;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.positions = (positions != null) ? positions : Collections.emptyList();
    }

    public boolean isPlaced() {
        return this.positions != null && !this.positions.isEmpty();
    }

}
