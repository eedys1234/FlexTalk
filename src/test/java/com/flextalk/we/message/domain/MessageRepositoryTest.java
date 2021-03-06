package com.flextalk.we.message.domain;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.message.cmmn.MockMessageFactory;
import com.flextalk.we.message.cmmn.MockMessageReader;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.repository.MessageFileRepository;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.MessageReadResponseDto;
import com.flextalk.we.participant.cmmn.ParticipantMatchers;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @DisplayName("????????? ?????? ?????????(TEXT)")
    @Test
    public void createTextMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "????????? ?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatchers.matchingParticipant(room, invitedUser);

        String messageContent = "????????? ???????????????.";
        String messageType = "TEXT";

        //when
        Message message = Message.create(participant, room, messageContent, messageType);
        Message sendMessage = messageRepository.save(message);

        //then
        assertThat(sendMessage, notNullValue());
        assertThat(sendMessage.getId(), greaterThan(0L));
    }


    @DisplayName("????????? ????????? ???????????? ????????? ?????? ?????? ?????????(FILE)")
    @Test
    public void createTextMessageInvalidMessageTypeExceptionTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "?????????_?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatchers.matchingParticipant(room, invitedUser);

        String messageContent = "????????? ???????????????.";
        String messageType = "FILE";

        //when, then
        assertThrows(IllegalArgumentException.class, () -> Message.create(participant, room, messageContent, messageType));
    }

    @DisplayName("????????? ?????? ?????????(FILE)")
    @Test
    public void createFileMessageTest() throws IOException {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "?????????_?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatchers.matchingParticipant(room, invitedUser);

        String messageContent = "????????? ???????????????.";
        String messageType = "FILE";
        String orgFileName = "????????????????????????.txt";

        //when
        Message message = Message.create(participant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);
        boolean isCreated = sendMessage.saveFile("?????????".getBytes());

        //then
        assertThat(sendMessage, notNullValue());
        assertThat(sendMessage.getId(), greaterThan(0L));
        assertThat(sendMessage.getMessageFile(), notNullValue());
        assertThat(sendMessage.getMessageFile().getId(), greaterThan(0L));
        assertThat(isCreated, is(Boolean.TRUE));
    }

    @DisplayName("?????? ???????????? ?????? ????????? ?????? ?????????")
    @Test
    public void createChildMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "?????????_?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = ParticipantMatchers.matchingParticipant(room, invitedUser);

        String parentMessageContent = "????????? ???????????????.";
        String parentMessageType = "TEXT";
        String childMessageContent = "?????? ????????? ???????????????.";
        String childMessageType = "TEXT";

        //when
        Message parentMessage = messageRepository.save(Message.create(participant, room, parentMessageContent, parentMessageType));
        parentMessage.comment(Message.create(participant, room, childMessageContent, childMessageType));

        //then
        assertThat(parentMessage.getChildMessages().size(), equalTo(1));
    }

    @DisplayName("????????? ????????? ??????")
    @Test
    public void messageReadTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "?????????_?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant ownerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        Participant invitedParticipant = ParticipantMatchers.matchingParticipant(room, invitedUser);

        String messageContent = "????????? ???????????????.";
        String messageType = "FILE";
        String orgFileName = "????????????????????????.txt";

        Message message = Message.create(invitedParticipant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);

        //when
        sendMessage.read(ownerParticipant);

        //then
        assertThat(sendMessage.getMessageReads().size(), equalTo(1));
    }

    @DisplayName("????????? ????????? ????????? ?????? ?????? ?????????")
    @Test
    public void messageReadOwnerExceptionTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "?????????_?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant invitedParticipant = ParticipantMatchers.matchingParticipant(room, invitedUser);

        String messageContent = "????????? ???????????????.";
        String messageType = "FILE";
        String orgFileName = "????????????????????????.txt";

        Message message = Message.create(invitedParticipant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> sendMessage.read(invitedParticipant));
    }

    @DisplayName("?????? ????????? ???????????? ?????????")
    @Test
    public void findOneMessageTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create("test1@gmail.com", "123!@#DDDDD");
        User invitedUser = mockUserFactory.create("test2@gmail.com", "123!@#DDDDD");

        userRepository.save(roomCreator);
        userRepository.save(invitedUser);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "?????????_?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant invitedParticipant = ParticipantMatchers.matchingParticipant(room, invitedUser);

        String messageContent = "????????? ???????????????.";
        String messageType = "FILE";
        String orgFileName = "????????????????????????.txt";

        Message message = Message.create(invitedParticipant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);

        //when
        Message findMessage = messageRepository.findOne(sendMessage.getId(), invitedParticipant.getId(), room.getId())
                .orElseThrow(() -> new NotEntityException("???????????? ?????? ??? ????????????."));

        //then
        assertThat(findMessage, equalTo(sendMessage));

    }

    @DisplayName("??? ???????????? ?????? ?????? ?????? ???????????? ?????????")
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
        String roomName = "?????????_?????????";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUsers);
        roomRepository.save(room);

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        List<Participant> participants = ParticipantMatchers.matchingNotRoomOwner(room);

        MockMessageFactory mockMessageFactory = new MockMessageFactory(room, roomOwnerParticipant);
        List<Message> messages = mockMessageFactory.createTextList();

        //?????? ????????? ??????
        MockMessageReader mockMessageReaderA = new MockMessageReader();
        mockMessageReaderA.addAll(participants);

        //2??? ??????
        MockMessageReader mockMessageReaderB = new MockMessageReader();
        mockMessageReaderB.addAll(participants.subList(0, 2));

        //1??? ??????
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
