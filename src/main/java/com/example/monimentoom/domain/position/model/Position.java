package com.example.monimentoom.domain.position.model;


import com.example.monimentoom.domain.goods.model.Goods;
import com.example.monimentoom.domain.position.type.WallSide;
import com.example.monimentoom.domain.room.Room;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "positions", indexes = {
        @Index(name = "idx_positions_room_id", columnList = "room_id"),
        @Index(name = "idx_positions_goods_id", columnList = "goods_id")
})
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
    private int widthUnit;
    @Column(nullable = false)
    private int heightUnit;

    @Builder
    public Position(Goods goods, Room room, WallSide wall, int x, int y, int widthUnit, int heightUnit) {
        this.goods = goods;
        this.room = room;
        this.wall = wall;
        this.x = x;
        this.y = y;
        this.widthUnit = widthUnit;
        this.heightUnit = heightUnit;
    }

    public void update(Room room, Integer x, Integer y, WallSide wallSide, Integer widthUnit, Integer heightUnit) {
        this.room = room;
        this.wall = wallSide;
        this.x = x;
        this.y = y;
        this.widthUnit = widthUnit;
        this.heightUnit = heightUnit;
    }
}
