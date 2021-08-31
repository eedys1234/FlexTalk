package com.flextalk.we.participant.cmmn;

import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;

import java.util.List;

public class MockParticipantFactory {

    private final Room room;

    public MockParticipantFactory(final Room room) {
        this.room = room;
    }

    public List<Participant> createList(List<User> users) {
        room.invite(users);
        return room.participants();
    }

}
