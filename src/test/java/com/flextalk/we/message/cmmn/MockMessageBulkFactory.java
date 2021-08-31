package com.flextalk.we.message.cmmn;

import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class MockMessageBulkFactory extends MockMessageFactory {

    public MockMessageBulkFactory(Room room, Participant participant) {
        super(room, participant);
    }

    @Override
    public List<Message> createTextListAddedId() {

        List<Message> totalMessages = new ArrayList<>();

        for(int i=0; i<1000; i++) {
            totalMessages.addAll(super.createTextList());
        }

        long id = 1L;

        for(Message message : totalMessages) {
            ReflectionTestUtils.setField(message, "id", id++);
        }

        return totalMessages;
    }
}
