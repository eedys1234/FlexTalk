package com.flextalk.we.participant.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_participant", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "user_id"}),
})
@EqualsAndHashCode(of = {"id"})
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_owner")
    private Boolean isOwner;

    @Column(name = "is_bookmark")
    private Boolean isBookMark;

    @Column(name = "is_alarm")
    private Boolean isAlarm;

    private Participant(Room room, User user) {
        this.room = Objects.requireNonNull(room);
        this.user = Objects.requireNonNull(user);
        this.isOwner = Boolean.FALSE;
        this.isBookMark = Boolean.FALSE;
        this.isAlarm = Boolean.TRUE;
    }

    private Participant(Room room, User user, Boolean isOwner) {
        this(room, user);
        this.isOwner = isOwner;
    }


    public boolean addBookMark() {
        this.isBookMark = Boolean.TRUE;
        return true;
    }

    public boolean deleteBookMark() {
        this.isBookMark = Boolean.FALSE;
        return true;
    }

    public boolean addAlarm() {
        this.isAlarm = Boolean.TRUE;
        return true;
    }

    public boolean deleteAlarm() {
        this.isAlarm = Boolean.FALSE;
        return true;
    }

    public static Participant of(Room room, User user) {
        return new Participant(room, user);
    }

    public static Participant of(Room room, User user, Boolean isOwner) {
        return new Participant(room, user, isOwner);
    }

    public void assignOwner() {
        this.isOwner = Boolean.TRUE;
    }

    public void resign() {
        this.isOwner = Boolean.FALSE;
    }

    public boolean isParticipant(User user) {
        return this.getUser().equals(user);
    }

}
