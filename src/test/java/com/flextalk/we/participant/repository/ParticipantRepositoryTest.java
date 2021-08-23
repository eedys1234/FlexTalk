package com.flextalk.we.participant.repository;

import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.repository.repository.ParticipantRepository;
import com.flextalk.we.room.cmmn.MockRoom;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.repository.RoomRepository;
import com.flextalk.we.user.cmmn.MockUserCollection;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import org.hamcrest.collection.IsArray;
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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsArray.*;
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

    public Room inviteRoom(List<User> users) {

        final String roomName = "채팅방";
        final String roomType = "GROUP";
        final int roomLimitCount = 10;

        Room room = mockRoom.create(users.get(0), roomName, roomType, roomLimitCount);

        for(int i=1;i<users.size();i++) {
            room.invite(users.get(i));
        }

        return roomRepository.save(room);
    }

    @DisplayName("제한된 인원 내 채팅방에 초대")
    @Test
    public void inviteParticipantTest() {

        //given
        List<User> users = getUsers();

        //when
        Room room = inviteRoom(users);

        //then
        Room findRoom = roomRepository.findOne(room.getId()).orElseThrow(() -> new NullPointerException(""));
        assertThat(findRoom.getParticipants().size(), equalTo(users.size()));
        assertThat(findRoom.getParticipants().stream()
                .map(Participant::getUser)
                .map(User::getId)
                .collect(toList()), containsInAnyOrder(users.stream()
                        .map(user -> equalTo(user.getId()))
                        .collect(toList())));
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


    @DisplayName("특정 채팅방에 참여하고 있는 참여자 리스트 조회")
    @Test
    public void getParticipantsByRoomTest() {

        //given
        List<User> users = getUsers();
        Room room = inviteRoom(users);

        //when
        List<Participant> participants = participantRepository.findByRoom(room);

        //then
        assertThat(users.size(), equalTo(participants.size()));
        assertThat(users, containsInAnyOrder(participants.stream()
                .map(participant -> equalTo(participant.getUser()))
                .collect(toList())));
    }
}
