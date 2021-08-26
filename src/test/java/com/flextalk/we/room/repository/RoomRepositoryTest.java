package com.flextalk.we.room.repository;

import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.repository.repository.ParticipantRepository;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomAlarm;
import com.flextalk.we.room.domain.entity.RoomBookMark;
import com.flextalk.we.room.domain.entity.RoomMessageDate;
import com.flextalk.we.room.domain.repository.RoomAlarmRepository;
import com.flextalk.we.room.domain.repository.RoomBookMarkRepository;
import com.flextalk.we.room.domain.repository.RoomMessageDateRepository;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private RoomBookMarkRepository roomBookMarkRepository;

    @Autowired
    private RoomAlarmRepository roomAlarmRepository;

    @Autowired
    private RoomMessageDateRepository roomMessageDateRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    private User registeredUser;

    @BeforeEach
    public void setup() {
        String email = "TEST@gmail.com";
        String password = "TEST1234";
        User user = User.register(email, password);
        registeredUser = userRepository.save(user);
    }

    @DisplayName("채팅방 생성 테스트")
    @Test
    public void createRoomTest() {

        //given
        String roomName = "사용자1";
        String roomType = "NORMAL";
        int room_limit_size = 2;

        //when
        Room room = Room.create(registeredUser, roomName, roomType, room_limit_size);
        Room createdRoom = roomRepository.save(room);

        //then
        assertThat(createdRoom.getId(), greaterThan(0L));
    }

    @DisplayName("채팅방의 참여자 제한을 넘을경우 IllegalArgumentException throw 해야한다.")
    @Test
    public void gtRoomParticipantTest() {

        //given
        String roomName = "사용자1";
        String roomType = "NORMAL";
        int room_limit_size = 10;

        //then
        assertThrows(IllegalArgumentException.class, () -> Room.create(registeredUser, roomName, roomType, room_limit_size));
    }

    @DisplayName("사용자의 채팅방 리스트 가져오기")
    @Test
    public void findUserRoomsTest() {

        /**
         * room 10
         * participant 10명
         * user 1명
         */

        //given
        List<Room> rooms = new MockRoomFactory(registeredUser).createList();
        LocalDateTime now = LocalDateTime.now().minusHours(1);

        for(Room room : rooms)
        {
            roomRepository.save(room);
        }

        //when
        List<Room> sortedRooms = roomRepository.findByUser(registeredUser);

        //then
        assertThat(sortedRooms.size(), equalTo(rooms.size()));
        assertThat(sortedRooms, equalTo(rooms.stream()
                .sorted(comparing(Room::getId))
                .collect(toList())));
    }

    @DisplayName("채팅방 삭제 시 참여자 정보, 최근 메시지 일자, 즐겨찾기, 알람 삭제 테스트")
    @Test
    public void deleteRoomTest() {

        //given
        MockRoomFactory mockRoom = new MockRoomFactory(registeredUser);

        String roomName = "TEST 채팅방1";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        Room room = mockRoom.create(roomName, roomType, roomLimitCount);
        room.addAlarm(registeredUser);
        room.addBookMark(registeredUser);
        room.updateRecentDate();

        Room createdRoom = roomRepository.save(room);

        Long roomId = createdRoom.getId();

        //when
        Long resValue = roomRepository.delete(room);

        //then
        Optional<Room> findRoom = roomRepository.findOne(roomId);
        Optional<RoomMessageDate> findRoomMessageDates = roomMessageDateRepository.findByRoomId(room);
        List<RoomBookMark> findBookMarks = roomBookMarkRepository.findByUser(registeredUser);
        List<RoomAlarm> findAlarms = roomAlarmRepository.findByUser(registeredUser);
        List<Participant> findParticipants = participantRepository.findByUser(registeredUser);

        assertThat(resValue, is(1L));
        assertThat(findRoom, equalTo(Optional.empty()));
        assertThat(findAlarms.size(), equalTo(0));
        assertThat(findBookMarks.size(), equalTo(0));
        assertThat(findParticipants.size(), equalTo(0));
        assertThat(findRoomMessageDates, equalTo(Optional.empty()));
    }

}
