package com.example.monimentoom.domain.Position;


import com.example.monimentoom.domain.Goods.Goods;
import com.example.monimentoom.domain.Room.Room;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WallSide wall;
    @Column(nullable = false)
    private int x;
    @Column(nullable = false)
    private int y;
    @Column(nullable = false)
    private int width_unit;
    @Column(nullable = false)
    private int height_unit;

    @Builder
    public Position(Goods goods, Room room, WallSide wall, int x, int y, int width_unit, int height_unit) {
        this.goods = goods;
        this.room = room;
        this.wall = wall;
        this.x = x;
        this.y = y;
        this.width_unit = width_unit;
        this.height_unit = height_unit;
    }
}
