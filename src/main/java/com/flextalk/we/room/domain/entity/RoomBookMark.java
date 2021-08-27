package com.flextalk.we.room.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_room_bookmark", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "room_id"})
})
public class RoomBookMark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    private RoomBookMark(User user, Room room) {
        this.user = Objects.requireNonNull(user);
        this.room = Objects.requireNonNull(room);
    }

    public static RoomBookMark of(User user, Room room) {
        RoomBookMark roomBookMark = new RoomBookMark(user, room);
        return roomBookMark;
    }



}
