package com.flextalk.we.message.repository;

import com.flextalk.we.cmmn.file.FileManager;
import com.flextalk.we.message.cmmn.MockMessageBulkFactory;
import com.flextalk.we.message.cmmn.MockMessageFactory;
import com.flextalk.we.message.cmmn.MockMessageReader;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.entity.MessageRead;
import com.flextalk.we.message.domain.repository.MessageFileRepository;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.MessageReadBulkInsertDto;
import com.flextalk.we.message.dto.MessageReadResponseDto;
import com.flextalk.we.participant.cmmn.ParticipantMatcher;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.participant.domain.repository.ParticipantRepository;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(value = "test")
@Transactional
public class MessageRepositoryTest {

    @Value("${message_file_path}")
    private String messageFilePath;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageReadRepository messageReadRepository;

    @Autowired
    private MessageFileRepository messageFileRepository;

    @DisplayName("메시지 생성 테스트(TEXT)")
    @Test
    public void createTextMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트 채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatcher.matchingParticipant(room, invitedUser);

        String messageContent = "테스트 채팅입니다.";
        String messageType = "TEXT";

        //when
        Message message = Message.create(participant, room, messageContent, messageType);
        Message sendMessage = messageRepository.save(message);

        //then
        assertThat(sendMessage, notNullValue());
        assertThat(sendMessage.getId(), greaterThan(0L));
    }


    @DisplayName("잘못된 메시지 타입으로 생성할 경우 예외 테스트(FILE)")
    @Test
    public void createTextMessageInvalidMessageTypeExceptionTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatcher.matchingParticipant(room, invitedUser);

        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";

        //when, then
        assertThrows(IllegalArgumentException.class, () -> Message.create(participant, room, messageContent, messageType));
    }

    @DisplayName("메시지 생성 테스트(FILE)")
    @Test
    public void createFileMessageTest() throws IOException {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatcher.matchingParticipant(room, invitedUser);

        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";
        String orgFileName = "개발자학습로드맵.txt";

        //when
        Message message = Message.create(participant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);
        boolean isCreated = sendMessage.saveFile("테스트".getBytes());

        //then
        assertThat(sendMessage, notNullValue());
        assertThat(sendMessage.getId(), greaterThan(0L));
        assertThat(sendMessage.getMessageFile(), notNullValue());
        assertThat(sendMessage.getMessageFile().getId(), greaterThan(0L));
        assertThat(isCreated, is(Boolean.TRUE));
    }

    @DisplayName("기존 메시지에 답장 메시지 생성 테스트")
    @Test
    public void createChildMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatcher.matchingParticipant(room, invitedUser);

        String parentMessageContent = "테스트 채팅입니다.";
        String parentMessageType = "TEXT";
        String childMessageContent = "답장 테스트 채팅입니다.";
        String childMessageType = "TEXT";

        //when
        Message parentMessage = messageRepository.save(Message.create(participant, room, parentMessageContent, parentMessageType));
        parentMessage.comment(Message.create(participant, room, childMessageContent, childMessageType));

        //then
        assertThat(parentMessage.getChildMessages().size(), equalTo(1));
    }

    @DisplayName("생성된 메시지 읽기")
    @Test
    public void messageReadTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant ownerParticipant = ParticipantMatcher.matingRoomOwner(room);
        Participant invitedParticipant = ParticipantMatcher.matchingParticipant(room, invitedUser);

        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";
        String orgFileName = "개발자학습로드맵.txt";

        Message message = Message.create(invitedParticipant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);

        //when
        sendMessage.read(ownerParticipant);

        //then
        assertThat(sendMessage.getMessageReads().size(), equalTo(1));
    }

    @DisplayName("자신이 생성한 메시지 읽기 예외 테스트")
    @Test
    public void messageReadOwnerExceptionTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");
        FileManager mockFileManager = mock(FileManager.class);

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant invitedParticipant = ParticipantMatcher.matchingParticipant(room, invitedUser);

        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";
        String orgFileName = "개발자학습로드맵.txt";

        Message message = Message.create(invitedParticipant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> sendMessage.read(invitedParticipant));
    }

    @DisplayName("메시지 읽기 테스트")
    @Test
    public void readMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트 채팅방";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);
        roomRepository.save(room);

        Participant roomOwnerParticipant = ParticipantMatcher.matingRoomOwner(room);
        Participant invitedParticipant = ParticipantMatcher.matchingNotRoomOwner(room).get(0);

        MockMessageFactory mockMessageFactory = new MockMessageBulkFactory(room, invitedParticipant);
        List<Message> messages = mockMessageFactory.createTextList();

        for(Message message : messages) {
            messageRepository.save(message);
        }

        List<MessageReadBulkInsertDto> messageReads = messages.stream()
                .map(message -> new MessageReadBulkInsertDto(roomOwnerParticipant.getId(), message.getId()))
                .collect(toList());

        //when
        messageReadRepository.saveAll(messageReads);

        //then
        List<MessageRead> allReads = messageReadRepository.findAll();
        assertThat(allReads.size(), equalTo(messages.size()));

    }

    @DisplayName("각 메시지에 대한 읽음 개수 가져오기 테스트")
    @Test
    public void readCountGroupByMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        List<User> invitedUsers = Arrays.asList(
            mockUserFactory.create("test2@gmail.com", "123!@#DDDDD"),
            mockUserFactory.create("test3@gmail.com", "123!@#DDDDD"),
            mockUserFactory.create("test4@gmail.com", "123!@#DDDDD")
        );

        userRepository.save(roomCreator);

        for(User user : invitedUsers) {
            userRepository.save(user);
        }

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUsers);
        roomRepository.save(room);

        Participant roomOwnerParticipant = ParticipantMatcher.matingRoomOwner(room);
        List<Participant> participants = ParticipantMatcher.matchingNotRoomOwner(room);

        MockMessageFactory mockMessageFactory = new MockMessageFactory(room, roomOwnerParticipant);
        List<Message> messages = mockMessageFactory.createTextList();

        //전체 참여자 그룹
        MockMessageReader mockMessageReaderA = new MockMessageReader();
        mockMessageReaderA.addAll(participants);

        //2명 그룹
        MockMessageReader mockMessageReaderB = new MockMessageReader();
        mockMessageReaderB.addAll(participants.subList(0, 2));

        //1명 그룹
        MockMessageReader mockMessageReaderC = new MockMessageReader();
        mockMessageReaderC.addAll(participants.subList(0, 1));

        List<Message> messagesA = messages.subList(0, 3);
        List<Message> messagesB = messages.subList(3, 6);
        List<Message> messagesC = messages.subList(6, 10);
        List<Long> expectedReads = new ArrayList<>();

        for(Message message : messagesA) {
            mockMessageReaderA.read(message);
            expectedReads.add(mockMessageReaderA.participantSize());
        }

        for(Message message : messagesB) {
            mockMessageReaderB.read(message);
            expectedReads.add(mockMessageReaderB.participantSize());
        }

        for(Message message : messagesC) {
            mockMessageReaderC.read(message);
            expectedReads.add(mockMessageReaderC.participantSize());
        }

        for(Message message : messages) {
            messageRepository.save(message);
        }

        List<MessageReadResponseDto> messageReads = messageReadRepository.findByMessages(messages.stream()
                .map(Message::getId)
                .collect(toList()));

        //then
        assertThat(messageReads.size(), equalTo(messages.size()));
        assertThat(messageReads.stream()
                        .map(MessageReadResponseDto::getMessageReadCount)
                        .collect(toList()),
                equalTo(expectedReads));
    }


}
