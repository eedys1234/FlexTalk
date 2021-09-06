package com.flextalk.we.message.domain;

import com.flextalk.we.message.cmmn.MockMessageBulkFactory;
import com.flextalk.we.message.cmmn.MockMessageFactory;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.repository.MessageReadJdbcRepository;
import com.flextalk.we.message.dto.MessageReadBulkInsertDto;
import com.flextalk.we.participant.cmmn.ParticipantMatchers;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
public class MessageJdbcRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private MockMessageReadJdbcRepository messageReadJdbcRepository;

    @BeforeEach
    public void init() {
        int batchSize = 5000;
        messageReadJdbcRepository = new MockMessageReadJdbcRepository(jdbcTemplate, batchSize);
    }

    @DisplayName("대량의 메시지 읽기 테스트")
    @Test
    public void readMessageTestByBulkInsert() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        String roomName = "테스트 채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        Participant invitedParticipant = ParticipantMatchers.matchingNotRoomOwner(room).get(0);

        long invitedParticipantId = 1L;
        ReflectionTestUtils.setField(invitedParticipant, "id", invitedParticipantId);

        MockMessageFactory mockMessageFactory = new MockMessageBulkFactory(room, roomOwnerParticipant);
        List<Message> messages = mockMessageFactory.createTextListAddedId();

        List<MessageReadBulkInsertDto> createdMessageRead = messages.stream()
                .map(message -> new MessageReadBulkInsertDto(invitedParticipant.getId(), message.getId()))
                .collect(toList());

        //when
        messageReadJdbcRepository.saveAll(createdMessageRead);

        //verify
        verify(jdbcTemplate, times(2)).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
    }
}
