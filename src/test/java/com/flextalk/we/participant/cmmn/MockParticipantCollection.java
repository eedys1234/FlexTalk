package com.flextalk.we.participant.cmmn;

import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;

public class MockParticipantCollection {

    public List<Participant> collect(List<Room> rooms, User user) {

        List<Participant> participants = new ArrayList<>();

        for(Room room : rooms) {
            participants.add(Participant.of(room, user));
        }

        return participants;
    }

}
