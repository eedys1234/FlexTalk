package com.flextalk.we.message.cmmn;

import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MockMessageFactory {

    private Room room;
    private Participant participant;

    private String[][] textMessageInfo = {
        {"테스트 메시지입니다_1", "TEXT"},
        {"테스트 메시지입니다_2", "TEXT"},
        {"테스트 메시지입니다_3", "TEXT"},
        {"테스트 메시지입니다_4", "TEXT"},
        {"테스트 메시지입니다_5", "TEXT"},
        {"테스트 메시지입니다_6", "TEXT"},
        {"테스트 메시지입니다_7", "TEXT"},
        {"테스트 메시지입니다_8", "TEXT"},
        {"테스트 메시지입니다_9", "TEXT"},
        {"테스트 메시지입니다_10", "TEXT"},
    };

    private String[][] fileMessageInfo = {
        {"테스트 메시지입니다_1", "FILE"},
        {"테스트 메시지입니다_2", "FILE"},
        {"테스트 메시지입니다_3", "FILE"},
        {"테스트 메시지입니다_4", "FILE"},
        {"테스트 메시지입니다_5", "FILE"},
        {"테스트 메시지입니다_6", "FILE"},
        {"테스트 메시지입니다_7", "FILE"},
        {"테스트 메시지입니다_8", "FILE"},
        {"테스트 메시지입니다_9", "FILE"},
        {"테스트 메시지입니다_10", "FILE"},
    };

    public MockMessageFactory(Room room, Participant participant) {
        this.room = room;
        this.participant = participant;
    }


    public List<Message> createTextList() {
        return Arrays.stream(textMessageInfo)
                .map(info -> Message.create(participant, room, info[0], info[1]))
                .collect(toList());
    }

    public List<Message> createTextListAddedId() {

        List<Message> messages = createTextList();

        long id = 1L;

        for(Message message : messages) {
            ReflectionTestUtils.setField(message, "id", id++);
        }
        return messages;
    }


}
