package com.flextalk.we.participant.repository;

import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.repository.repository.ParticipantRepository;
import com.flextalk.we.room.cmmn.MockRoom;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.repository.RoomRepository;
import com.flextalk.we.user.cmmn.MockUserCollection;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ParticipantRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    private MockUserCollection userCollection;
    private MockRoom mockRoom;

    @BeforeEach
    public void setup() {
        userCollection = new MockUserCollection();
        mockRoom = new MockRoom();
        addUsers();
    }

    /**
     * helper method
     */
    public void addUsers() {
        List<User> users = userCollection.create();
        for(User user : users) {
            userRepository.save(user);
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @DisplayName("제한된 인원 내 채팅방에 초대")
    @Test
    public void inviteParticipantTest() {

        //given
        final String roomName = "채팅방";
        final String roomType = "GROUP";
        final int roomLimitCount = 10;

        List<User> users = getUsers();
        Room room = mockRoom.create(users.get(0), roomName, roomType, roomLimitCount);

        //when
        for(int i=1;i<users.size();i++) {
            room.invite(users.get(i));
        }

        Room createRoom = roomRepository.save(room);

        //then
        assertThat(createRoom.getParticipants().size(), equalTo(roomLimitCount));
    }

    @DisplayName("제한된 인원 이상 채팅방에 초대")
    @Test
    public void overInviteParticipantExceptionTest() {

        //given
        final String roomName = "채팅방";
        final String roomType = "GROUP";
        final int roomLimitCount = 9;

        List<User> users = getUsers();
        Room room = mockRoom.create(users.get(0), roomName, roomType, roomLimitCount);

        //then
        assertThrows(IllegalStateException.class, () -> {

            for(int i=1;i<users.size();i++) {
                room.invite(users.get(i));
            }
            roomRepository.save(room);
        });
    }

    @DisplayName("이미 초대된 사용자 채팅방에 초대")
    @Test
    public void alreadyInviteParticipantExceptionTest() {

        //given
        final String roomName = "채팅방";
        final String roomType = "GROUP";
        final int roomLimitCount = 9;

        List<User> users = getUsers();
        Room room = mockRoom.create(users.get(0), roomName, roomType, roomLimitCount);

        //when
        room.invite(users.get(1));

        //then
        assertThrows(IllegalArgumentException.class, () -> room.invite(users.get(1)));
    }
}
