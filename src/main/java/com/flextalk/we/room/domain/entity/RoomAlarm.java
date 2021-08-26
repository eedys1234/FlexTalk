package com.flextalk.we.room.domain.entity;

import com.flextalk.we.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_room_alarm", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user", "room"})
})
public class RoomAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    private Room room;

    private RoomAlarm(User user, Room room) {
        this.user = Objects.requireNonNull(user);
        this.room = Objects.requireNonNull(room);
    }

    public static RoomAlarm of(User user, Room room) {
        RoomAlarm roomAlarm = new RoomAlarm(user, room);
        return roomAlarm;
    }
}
