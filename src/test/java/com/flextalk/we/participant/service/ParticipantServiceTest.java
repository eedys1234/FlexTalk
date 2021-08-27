package com.flextalk.we.participant.service;

import com.flextalk.we.participant.cmmn.MockParticipantFactory;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.repository.repository.ParticipantRepository;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.service.RoomService;
import com.flextalk.we.user.cmmn.MockLimitUserFactory;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {

    @InjectMocks
    private ParticipantService participantService;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private UserService userService;

    @Mock
    private RoomService roomService;


    private Room getRoom(User user, int roomLimitCount) {
        MockRoomFactory mockRoomFactory = new MockRoomFactory(user);
        String roomName = "테스트 채팅방";
        String roomType = "GROUP";
        Long roomId = 1L;

        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);
        return room;
    }

    @DisplayName("채팅방의 참여자 리스트 가져오기 테스트")
    @Test
    public void getParticipantsByRoomTest() {

        //given
        int roomLimitCount = 11;
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(0L);
        Room room = getRoom(roomCreator, roomLimitCount);
        MockParticipantFactory mockParticipantFactory = new MockParticipantFactory(room);
        List<Participant> participants = mockParticipantFactory.createList(mockUserFactory.createListAddedId());

        doReturn(room).when(roomService).findRoom(anyLong());
        doReturn(participants).when(participantRepository).findByRoom(any(Room.class));

        //when
        List<ParticipantResponseDto> participantsByRoom = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(participantsByRoom.size(), equalTo(participants.size()));
        assertThat(participantsByRoom.stream().map(participantResponseDto -> participantResponseDto.getUserEmail())
                .collect(toList()),
                equalTo(participants.stream().map(participant -> participant.getUser().getEmail()).collect(toList())));


        //verify
        verify(roomService, times(1)).findRoom(anyLong());
        verify(participantRepository, times(1)).findByRoom(any(Room.class));
    }


    @DisplayName("채팅방에 참여자 초대하기 테스트(999명)")
    @Test
    public void inviteParticipant() {

        //given
        int roomLimitCount = 1000;
        MockUserFactory mockUserFactory = new MockLimitUserFactory();
        User roomCreator = mockUserFactory.createAddedId(0L);
        Room room = getRoom(roomCreator, roomLimitCount);

        MockUserFactory extendedUserFactory = new MockLimitUserFactory();
        List<User> users = extendedUserFactory.createListAddedId().subList(0, 999);

        doReturn(users).when(userService).findUsers(any());
        doReturn(room).when(roomService).findRoomAddedAddiction(anyLong());

        //when
        List<Long> participantIds = participantService.inviteParticipants(room.getId(), users.stream()
                .map(user -> String.valueOf(user.getId()))
                .collect(joining(",")));

        //then(자기 자신 제외)
        assertThat(participantIds.size(), equalTo(roomLimitCount - 1));

        //verify
        verify(userService, times(1)).findUsers(any());
        verify(roomService, times(1)).findRoomAddedAddiction(anyLong());
    }

    @DisplayName("채팅방에 참여자가 떠날경우 테스트")
    @Test
    public void leaveParticipantsTest() {

        //given
        int roomLimitCount = 100;
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(0L);
        Room room = getRoom(roomCreator, roomLimitCount);

        User invitedUser = mockUserFactory.createAddedId(1L);
        room.invite(invitedUser);
        Long invitedParticipantId = 1L;

        Participant invitedParticipant = room.getParticipants().stream()
                .filter(Participant::getIsOwner)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("초대된 참여자가 존재하지 않습니다."));

        ReflectionTestUtils.setField(invitedParticipant, "id", invitedParticipantId);

        doReturn(Optional.ofNullable(invitedParticipant)).when(participantRepository).findOne(anyLong());
        doReturn(room).when(roomService).findRoomAddedAddiction(anyLong());

        //when
        Long leaveParticipantId = participantService.leaveParticipant(room.getId(), invitedParticipantId);

        //then
        assertThat(leaveParticipantId, equalTo(invitedParticipantId));

        //verify
        verify(participantRepository, times(1)).findOne(anyLong());
        verify(roomService, times(1)).findRoomAddedAddiction(anyLong());
    }

    @DisplayName("채팅방 참여자 추방하기 테스트(999명)")
    @Test
    public void deportParticipantsTest() {

        //given
        int roomLimitCount = 1000;
        MockUserFactory mockUserFactory = new MockLimitUserFactory();
        User roomCreator = mockUserFactory.createAddedId(0L);
        Room room = getRoom(roomCreator, roomLimitCount);

        MockUserFactory extendedUserFactory = new MockLimitUserFactory();
        List<User> users = extendedUserFactory.createListAddedId().subList(0, 999);

        Participant roomOwner = room.participants().stream()
                .filter(Participant::getIsOwner)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("채팅방 생성자가 존재하지 않습니다."));

        long id = 1L;
        ReflectionTestUtils.setField(roomOwner, "id", id);

        room.invite(users);

        List<Participant> participants = room.participants();

        for(Participant participant : participants)
        {
            if(!participant.getIsOwner()) {
                ReflectionTestUtils.setField(participant, "id", ++id);
            }
        }

        doReturn(participants).when(participantRepository).findByIds(any());
        doReturn(Optional.ofNullable(roomOwner)).when(participantRepository).findOwner(anyLong());
        doReturn(room).when(roomService).findRoomAddedAddiction(anyLong());

        //when
        List<Long> deportParticipants = participantService.deportParticipants(room.getId(), roomOwner.getId(),
                participants.stream().map(part -> String.valueOf(part.getId())).collect(joining(",")));

        //then
        assertThat(deportParticipants.size(), equalTo(participants.size()));
        assertThat(deportParticipants, equalTo(participants.stream().map(Participant::getId).collect(toList())));

        //verify
        verify(participantRepository, times(1)).findByIds(any());
        verify(participantRepository, times(1)).findOwner(anyLong());
        verify(roomService, times(1)).findRoomAddedAddiction(anyLong());
    }

    @DisplayName("채팅방에 대한 권한을 전달하는 테스트")
    @Test
    public void promotePermissionTest() {

        //given
        int roomLimitCount = 2;
        MockUserFactory mockUserFactory = new MockLimitUserFactory();
        User roomCreator = mockUserFactory.createAddedId(0L);
        Room room = getRoom(roomCreator, roomLimitCount);

        Participant roomOwner = room.participants().stream()
                .filter(Participant::getIsOwner)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("채팅방 생성자가 존재하지 않습니다."));

        ReflectionTestUtils.setField(roomOwner, "id", 1L);

        User invitedUser = mockUserFactory.createAddedId(1L);
        room.invite(invitedUser);

        Participant promoteParticipant  = room.participants().stream()
                .filter(part -> !part.getIsOwner())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("채팅방 생성자가 존재하지 않습니다."));

        ReflectionTestUtils.setField(promoteParticipant, "id", 2L);

        doReturn(Optional.ofNullable(roomOwner)).when(participantRepository).findOwner(anyLong());
        doReturn(Optional.ofNullable(promoteParticipant)).when(participantRepository).findOne(anyLong());

        //when
        Long promoteParticipantId = participantService.promotePermission(roomOwner.getId(), promoteParticipant.getId());

        //then
        assertThat(promoteParticipantId, equalTo(promoteParticipant.getId()));

        //verify
        verify(participantRepository, times(1)).findOwner(anyLong());
        verify(participantRepository, times(1)).findOne(anyLong());
    }

}
