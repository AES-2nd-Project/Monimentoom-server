package com.example.monimentoom.domain.goods;

import com.example.monimentoom.domain.position.Position;
import com.example.monimentoom.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    private List<Position> positions = new ArrayList<>();

    @Builder
    public Goods(User user, String name, String imageUrl, String description, Integer price, List<Position> positions) {
        this.user = user;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.positions = (positions != null) ? positions : Collections.emptyList();
    }

}
