package com.flextalk.we.message.repository;

import com.flextalk.we.cmmn.file.FileManager;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.repository.MessageFileRepository;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.MessageReadResponseDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("{message_file_path}")
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
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUser);

        roomRepository.save(room);

        Participant participant = room.participants().stream().filter(part -> part.isParticipant(invitedUser)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

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

        Participant participant = room.participants().stream().filter(part -> part.isParticipant(invitedUser)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";

        //when, then
        assertThrows(IllegalArgumentException.class, () -> Message.create(participant, room, messageContent, messageType));
    }

    //TODO : 파일저장로직 어떻게 할 건지 고민
    @DisplayName("메시지 생성 테스트(FILE)")
    @Test
    public void createFileMessageTest() {

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

        Participant participant = room.participants().stream().filter(part -> part.isParticipant(invitedUser)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";
        String orgFileName = "개발자학습로드맵.txt";

        //when
        Message message = Message.create(participant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);

        //then
        assertThat(sendMessage, notNullValue());
        assertThat(sendMessage.getId(), greaterThan(0L));
        assertThat(sendMessage.getMessageFile(), notNullValue());
        assertThat(sendMessage.getMessageFile().getId(), greaterThan(0L));

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

        Participant participant = room.participants().stream().filter(part -> part.isParticipant(invitedUser)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

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

        Participant ownerParticipant = room.participants().stream().filter(Participant::getIsOwner).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

        Participant invitedParticipant = room.participants().stream().filter(part -> part.isParticipant(invitedUser)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));


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

        Participant invitedParticipant = room.participants().stream().filter(part -> part.isParticipant(invitedUser)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));


        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";
        String orgFileName = "개발자학습로드맵.txt";

        Message message = Message.create(invitedParticipant, room, messageContent, messageType, messageFilePath, orgFileName);
        Message sendMessage = messageRepository.save(message);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> sendMessage.read(invitedParticipant));
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
        userRepository.save(invitedUsers.get(0));
        userRepository.save(invitedUsers.get(1));
        userRepository.save(invitedUsers.get(2));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트_채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);

        room.invite(invitedUsers);

        roomRepository.save(room);

        Participant ownerParticipant = room.participants().stream().filter(Participant::getIsOwner).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

        List<Participant> participants = room.participants().stream()
                .filter(part -> !part.getIsOwner())
                .collect(toList());

        String messageContent = "테스트 채팅입니다.";
        String messageType = "FILE";
        String orgFileName = "개발자학습로드맵.txt";

        Message sendMessageA = messageRepository.save(Message.create(ownerParticipant, room, messageContent, messageType, messageFilePath, orgFileName));
        Message sendMessageB = messageRepository.save(Message.create(ownerParticipant, room, messageContent, messageType, messageFilePath, orgFileName));
        Message sendMessageC = messageRepository.save(Message.create(ownerParticipant, room, messageContent, messageType, messageFilePath, orgFileName));
        Message sendMessageD = messageRepository.save(Message.create(ownerParticipant, room, messageContent, messageType, messageFilePath, orgFileName));

        read(sendMessageA, participants); //모두읽음
        read(sendMessageB, participants.subList(0, 2)); //2명읽음
        read(sendMessageC, participants.subList(0, 1)); //1명읽음

        List<Message> messages = new ArrayList<>();

        messages.add(sendMessageA);
        messages.add(sendMessageB);
        messages.add(sendMessageC);
        messages.add(sendMessageD);

        List<MessageReadResponseDto> messageReads = messageReadRepository.findByMessages(
                messages.stream()
                        .map(Message::getId)
                        .collect(toList()));

        //then
        assertThat(messageReads.size(), equalTo(messages.subList(0, 3).size()));
        assertThat(messageReads.stream().map(MessageReadResponseDto::getMessageReadCount).collect(toList()),
                contains(3L, 2L, 1L));
    }

    private void read(Message message, List<Participant> participants) {
        for(Participant participant : participants) {
            message.read(participant);
        }
    }


}
