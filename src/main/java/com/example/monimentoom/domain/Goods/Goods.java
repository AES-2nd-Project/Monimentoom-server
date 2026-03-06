package com.example.monimentoom.domain.Goods;

import com.example.monimentoom.domain.User.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    @Column(nullable = false)
    private String image_url;

    private String description;

    @Builder
    public Goods(User user, String name, String image_url, String description) {
        this.user = user;
        this.name = name;
        this.image_url = image_url;
        this.description = description;
    }
}
