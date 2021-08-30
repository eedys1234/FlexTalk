package com.flextalk.we.message.service;

import com.flextalk.we.message.cmmn.MockMessageBulkFactory;
import com.flextalk.we.message.cmmn.MockMessageFactory;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.MessageReadBulkInsertDto;
import com.flextalk.we.message.dto.MessageReadUpdateDto;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.participant.service.ParticipantService;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private MessageReadRepository messageReadRepository;

    @Mock
    private MessageRepository messageRepository;

    @DisplayName("메시지 생성 테스트(TEXT)")
    @Test
    public void sendTextMessageTest() {

        //given

        //when

        //then

        //verify
    }

    @DisplayName("메시지 생성 테스트(FILE)")
    @Test
    public void sendFileMessageTest() {

        //given

        //when

        //then

        //verify
    }

    @DisplayName("최근 메시지 리스트 가져오기 테스트")
    @Test
    public void unReadMessageCountTest() {

        //given

        //when

        //then

        //verify

    }

    @DisplayName("메시지 읽기 테스트")
    @Test
    public void readMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        User invitedUser = mockUserFactory.createAddedId(2L);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트채팅방";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        Participant roomParticipant = room.participants().stream()
                .filter(part -> part.getIsOwner())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

        Participant participant = room.participants().stream()
                .filter(part -> !part.getIsOwner())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

        ReflectionTestUtils.setField(participant, "id", 2L);

        MockMessageFactory mockMessageFactory = new MockMessageBulkFactory(room, participant);
        List<Message> messages = mockMessageFactory.createTextListAddedId();

        List<MessageReadBulkInsertDto> messageReads = messages.stream()
                .map(message -> new MessageReadBulkInsertDto(roomCreator.getId(), message.getId()))
                .collect(toList());

        doReturn(roomParticipant).when(participantService).findParticipant(anyLong());
        doNothing().when(messageReadRepository).saveAll(any());

        MessageReadUpdateDto messageReadUpdateDto = new MessageReadUpdateDto();
        ReflectionTestUtils.setField(messageReadUpdateDto, "messageIds",
                messages.stream().map(message -> String.valueOf(message.getId())).collect(joining(",")));

        //when
        List<Long> readMessageIds = messageService.readMessage(roomCreator.getId(), messageReadUpdateDto);

        //then
        assertThat(readMessageIds.size(), equalTo(messages.size()));

        //verify
        verify(participantService, times(1)).findParticipant(anyLong());
        verify(messageReadRepository, times(1)).saveAll(any());

    }
}
