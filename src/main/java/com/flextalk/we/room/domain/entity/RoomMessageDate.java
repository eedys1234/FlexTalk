package com.flextalk.we.room.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_room_message_date")
public class RoomMessageDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_message_date_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", unique = true)
    private Room room;

    @Column(name = "room_message_recent_dt")
    private LocalDateTime roomMessageRecentDate;

    private RoomMessageDate(Room room) {
        this.room = Objects.requireNonNull(room);
    }

    public static RoomMessageDate generate(Room room) {
        RoomMessageDate roomMessageDate = new RoomMessageDate(room);
        roomMessageDate.update();
        return roomMessageDate;
    }

    public void update() {
        this.roomMessageRecentDate = LocalDateTime.now();
    }

    public void update(LocalDateTime now) {
        this.roomMessageRecentDate = now;
    }
}
