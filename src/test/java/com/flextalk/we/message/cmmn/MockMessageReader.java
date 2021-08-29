package com.flextalk.we.message.cmmn;

import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.participant.repository.entity.Participant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockMessageReader {

    private Set<Participant> participants = new HashSet<>();

    public void add(Participant participant) {
        this.participants.add(participant);
    }

    public void addAll(List<Participant> participants) {
        this.participants.addAll(participants);
    }

    public long participantSize() {
        return this.participants.size();
    }

    public void read(Message message) {

        for(Participant participant : participants) {
            message.read(participant);
        }
    }
}
