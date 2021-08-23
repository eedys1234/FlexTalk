package com.flextalk.we.participant.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_participant")
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

    private Participant(Room room, User user) {
        this.room = Objects.requireNonNull(room);
        this.user = Objects.requireNonNull(user);
    }

    private Participant(Room room, User user, Boolean isOwner) {
        this(room, user);
        this.isOwner = isOwner;
    }

    public static Participant of(Room room, User user) {
        Participant participant = new Participant(room, user);
        return participant;
    }

    public static Participant of(Room room, User user, Boolean isOwner) {
        Participant participant = of(room, user, isOwner);
        return participant;
    }

    public void assignOwner() {
        this.isOwner = true;
    }

    public boolean isParticipant(User user) {
        return this.getUser().equals(user);
    }

}
