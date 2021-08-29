package com.flextalk.we.room.repository;

import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.participant.domain.repository.ParticipantRepository;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomMessageDate;
import com.flextalk.we.room.domain.repository.RoomMessageDateRepository;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomMessageDateRepository roomMessageDateRepository;

    @Autowired
    private ParticipantRepository participantRepository;


    @DisplayName("채팅방 생성 테스트")
    @Test
    public void createRoomTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create();
        userRepository.save(user);

        String roomName = "사용자1";
        String roomType = "NORMAL";
        int roomLimitCount = 2;

        MockRoomFactory mockRoomFactory = new MockRoomFactory(user);

        //when
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        Room createdRoom = roomRepository.save(room);

        //then
        assertThat(createdRoom.getId(), greaterThan(0L));
    }

    @DisplayName("채팅방의 참여자 제한을 넘을경우 IllegalArgumentException throw 해야한다.")
    @Test
    public void gtRoomParticipantTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create();
        userRepository.save(user);

        String roomName = "사용자1";
        String roomType = "NORMAL";
        int roomLimitCount = 10;

        MockRoomFactory mockRoomFactory = new MockRoomFactory(user);

        //then
        assertThrows(IllegalArgumentException.class, () -> mockRoomFactory.create(roomName, roomType, roomLimitCount));
    }

    @DisplayName("사용자의 채팅방 리스트 가져오기")
    @Test
    public void findUserRoomsTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create();
        userRepository.save(user);

        List<Room> rooms = new MockRoomFactory(user).createList();

        for(Room room : rooms)
        {
            roomRepository.save(room);
        }

        //when
        List<RoomResponseDto> sortedRooms = roomRepository.findByUser(user);

        //then
        assertThat(sortedRooms.size(), equalTo(rooms.size()));
        assertThat(sortedRooms.stream().map(RoomResponseDto::getRoomId).collect(toList()),
                equalTo(rooms.stream().sorted(comparing(Room::getId)).map(Room::getId).collect(toList())));
    }

    @DisplayName("채팅방 삭제 시 참여자 정보, 최근 메시지 일자, 즐겨찾기, 알람 삭제 테스트")
    @Test
    public void deleteRoomTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create();
        userRepository.save(roomCreator);

        MockRoomFactory mockRoom = new MockRoomFactory(roomCreator);
        String roomName = "TEST 채팅방1";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        Room room = mockRoom.create(roomName, roomType, roomLimitCount);

        User invitedUser = mockUserFactory.create();
        room.invite(invitedUser);

        room.updateRecentDate();

        Room createdRoom = roomRepository.save(room);

        Long roomId = createdRoom.getId();

        //when
        Long resValue = roomRepository.delete(room);

        //then
        Optional<Room> findRoom = roomRepository.findOne(roomId);
        Optional<RoomMessageDate> findRoomMessageDates = roomMessageDateRepository.findByRoomId(room);
        List<Participant> findParticipants = participantRepository.findByUser(roomCreator);

        assertThat(resValue, is(1L));
        assertThat(findRoom, equalTo(Optional.empty()));
        assertThat(findParticipants.size(), equalTo(0));
        assertThat(findRoomMessageDates, equalTo(Optional.empty()));
    }

}
