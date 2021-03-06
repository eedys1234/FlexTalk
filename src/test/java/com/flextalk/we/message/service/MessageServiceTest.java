package com.flextalk.we.message.service;

import com.flextalk.we.message.cmmn.MockMessageBulkFactory;
import com.flextalk.we.message.cmmn.MockMessageFactory;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.repository.MessageReadJdbcRepository;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.*;
import com.flextalk.we.participant.cmmn.ParticipantMatchers;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.participant.service.ParticipantService;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.service.RoomService;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private RoomService roomService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private MessageReadRepository messageReadRepository;

    @Mock
    private MessageReadJdbcRepository messageReadJdbcRepository;

    @Mock
    private MessageRepository messageRepository;

    private String messageFilePath;

    @BeforeEach
    public void init() {
        messageFilePath = "/test/";
    }

    @DisplayName("????????? ?????? ?????????(TEXT)")
    @Test
    public void sendTextMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);

        String roomName = "????????? ?????????";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        long participantId = 1L;
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", participantId);

        MockMessageFactory mockMessageFactory = new MockMessageFactory(room, roomOwnerParticipant);
        List<Message> messages = mockMessageFactory.createTextListAddedId();

        Message message = messages.get(0);

        MessageSaveRequestDto messageSaveRequestDto = new MessageSaveRequestDto();
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageContent", message.getMessageContent());
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageType", message.getMessageType().name());

        doReturn(room).when(roomService).findRoomAddedAddiction(anyLong());
        doReturn(roomOwnerParticipant).when(participantService).findParticipant(anyLong());
        doReturn(message).when(messageRepository).save(any());

        //when
        Long sendMessageId = messageService.sendTextMessage(room.getId(), roomOwnerParticipant.getId(), messageSaveRequestDto);

        //then
        assertThat(sendMessageId, equalTo(message.getId()));

        //verify
        verify(roomService, times(1)).findRoomAddedAddiction(anyLong());
        verify(participantService, times(1)).findParticipant(anyLong());
        verify(messageRepository, times(1)).save(any());
    }

    @DisplayName("????????? ?????? ?????????(FILE)")
    @Test
    public void sendFileMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);

        String roomName = "????????? ?????????";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        long participantId = 1L;
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", participantId);

        MockMessageFactory mockMessageFactory = new MockMessageFactory(room, roomOwnerParticipant);

        List<Message> messages = mockMessageFactory.createFileListAddedId(messageFilePath);

        Message message = spy(messages.get(0));

        MessageSaveRequestDto messageSaveRequestDto = new MessageSaveRequestDto();
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageContent", message.getMessageContent());
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageType", message.getMessageType().name());

        boolean isCreated = true;

        doReturn(room).when(roomService).findRoomAddedAddiction(anyLong());
        doReturn(roomOwnerParticipant).when(participantService).findParticipant(anyLong());
        doReturn(message).when(messageRepository).save(any(Message.class));
        lenient().doReturn(isCreated).when(spy(message)).saveFile(any());

        String orgFileName = "???????????????.txt";
        byte[] file = "Hello, World!".getBytes();
        MessageService mockMessageService = new MockMessageService(roomService, participantService, messageRepository, messageReadRepository, messageReadJdbcRepository, messageFilePath);

        //when
        Long sendMessageId = mockMessageService.sendFileMessage(room.getId(), roomOwnerParticipant.getId(), messageSaveRequestDto, orgFileName,file);

        //then
        assertThat(sendMessageId, equalTo(message.getId()));

        //verify
        verify(roomService, times(1)).findRoomAddedAddiction(anyLong());
        verify(participantService, times(1)).findParticipant(anyLong());
        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(message, times(1)).saveFile(any());
    }

    @DisplayName("?????????(TEXT) ?????? ?????????")
    @Test
    public void deleteTextMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);

        String roomName = "????????? ?????????";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        long participantId = 1L;
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", participantId);

        MockMessageFactory mockMessageFactory = new MockMessageFactory(room, roomOwnerParticipant);
        List<Message> messages = mockMessageFactory.createTextListAddedId();

        Message message = messages.get(0);
        doReturn(Optional.ofNullable(message)).when(messageRepository).findOne(anyLong(), anyLong(), anyLong());

        //when
        Long deleteMessageId = messageService.deleteMessage(message.getId(), roomOwnerParticipant.getId(), room.getId());

        //then
        assertThat(deleteMessageId, equalTo(message.getId()));

        //verify
        verify(messageRepository, times(1)).findOne(anyLong(), anyLong(), anyLong());
    }

    @DisplayName("?????????(FILE) ?????? ?????????")
    @Test
    public void deleteFileMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);

        String roomName = "????????? ?????????";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        long participantId = 1L;
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", participantId);

        MockMessageFactory mockMessageFactory = new MockMessageFactory(room, roomOwnerParticipant);

        List<Message> messages = mockMessageFactory.createFileListAddedId(messageFilePath);

        boolean isDeleted = true;
        Message message = spy(messages.get(0));

        doReturn(Optional.ofNullable(message)).when(messageRepository).findOne(anyLong(), anyLong(), anyLong());
        doReturn(isDeleted).when(message).deleteFile();

        //when
        Long deleteMessageId = messageService.deleteMessage(message.getId(), roomOwnerParticipant.getId(), room.getId());

        //then
        assertThat(deleteMessageId, equalTo(message.getId()));

        //verify
        verify(messageRepository, times(1)).findOne(anyLong(), anyLong(), anyLong());
        verify(message, times(1)).deleteFile();
    }

    @DisplayName("??? ?????? ??? ???????????? ?????????")
    @Test
    public void unReadMessageCountTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(
                mockUserFactory.createAddedId(2L),
                mockUserFactory.createAddedId(3L),
                mockUserFactory.createAddedId(4L)
        );

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "????????? ?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;

        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);
        room.invite(users);

        long participantId = 1L;
        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", participantId);

        MockMessageFactory mockMessageFactory = new MockMessageBulkFactory(room, roomOwnerParticipant);
        List<Message> messages = mockMessageFactory.createTextListAddedId();

        String messageIds = messages.stream()
                .map(message -> String.valueOf(message.getId()))
                .collect(joining(","));

        long readCount = 1L;
        long unReadCount = room.participants().size() - readCount - 1;

        doReturn(room).when(roomService).findRoomAddedAddiction(anyLong());
        doReturn(messages.stream()
                .map(message -> new MessageReadResponseDto(message.getId(), readCount))
                .collect(toList())).when(messageReadRepository).findByMessages(any());


        //when
        List<MessageUnReadResponseDto> unReads = messageService.unReadMessagesCount(room.getId(), messageIds);

        //then
        assertThat(unReads.size(), equalTo(messages.size()));
        assertThat(unReads.stream()
                .map(MessageUnReadResponseDto::getMessageUnReadCount)
                .collect(toList()), equalTo(messages.stream()
                .map(message -> unReadCount)
                .collect(toList())));

        //verify
        verify(roomService, times(1)).findRoomAddedAddiction(anyLong());
        verify(messageReadRepository, times(1)).findByMessages(any());
    }

    @DisplayName("????????? ?????? ?????????")
    @Test
    public void readMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        User invitedUser = mockUserFactory.createAddedId(2L);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "??????????????????";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);

        Participant participant = ParticipantMatchers.matchingNotRoomOwner(room).get(0);

        long participantId = 2L;
        ReflectionTestUtils.setField(participant, "id", participantId);

        MockMessageFactory mockMessageFactory = new MockMessageBulkFactory(room, participant);
        List<Message> messages = mockMessageFactory.createTextListAddedId();

        List<MessageReadBulkInsertDto> messageReads = messages.stream()
                .map(message -> new MessageReadBulkInsertDto(roomCreator.getId(), message.getId()))
                .collect(toList());

        doReturn(roomOwnerParticipant).when(participantService).findParticipant(anyLong());
        doNothing().when(messageReadJdbcRepository).saveAll(any());

        MessageReadUpdateDto messageReadUpdateDto = new MessageReadUpdateDto();
        ReflectionTestUtils.setField(messageReadUpdateDto, "participantId", participantId);
        ReflectionTestUtils.setField(messageReadUpdateDto, "messageIds",
                messages.stream().map(message -> String.valueOf(message.getId())).collect(joining(",")));


        long isSuccess = 1L;

        //when
        Long isRead = messageService.readMessage(messageReadUpdateDto);

        //then
        assertThat(isRead, equalTo(isSuccess));

        //verify
        verify(participantService, times(1)).findParticipant(anyLong());
        verify(messageReadJdbcRepository, times(1)).saveAll(any());
    }


}
