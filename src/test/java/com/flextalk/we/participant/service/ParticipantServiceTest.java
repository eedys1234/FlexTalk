package com.flextalk.we.participant.service;

import com.flextalk.we.participant.cmmn.MockParticipantFactory;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.repository.repository.ParticipantRepository;
import com.flextalk.we.participant.service.ParticipantService;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
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
    private RoomRepository roomRepository;

    private MockUserFactory mockUserFactory;

    @BeforeEach
    public void setup() {
        mockUserFactory = new MockUserFactory();
    }

    //TODO : RoomServiceTest 클래스와 ParticipantServiceTest 클래스가 동일한 메서드를 사용한다. 중복발생
    //TODO : 별도의 객체로 생성해야하지만 어떤 객체를 생성하는게 좋을까 고민
    private User getUser() {
        User user = mockUserFactory.createAddedId(0L);
        return user;
    }

    private Room getRoom(User user) {
        MockRoomFactory mockRoomFactory = new MockRoomFactory(user);
        String roomName = "테스트 채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 11;
        Long roomId = 1L;

        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);
        return room;
    }


    @DisplayName("채팅방의 참여자 리스트 가져오기 테스트")
    @Test
    public void getParticipantsByRoomTest() {

        //given
        User user = getUser();
        Room room = getRoom(user);
        MockParticipantFactory mockParticipantFactory = new MockParticipantFactory(room);
        List<Participant> participants = mockParticipantFactory.createList(mockUserFactory.createListAddedId());

        doReturn(Optional.ofNullable(room)).when(roomRepository).findOne(anyLong());
        doReturn(participants).when(participantRepository).findByRoom(any(Room.class));

        //when
        List<ParticipantResponseDto> participantsByRoom = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(participantsByRoom.size(), equalTo(participants.size()));
        assertThat(participantsByRoom.stream().map(participantResponseDto -> participantResponseDto.getUserEmail())
                .collect(toList()),
                equalTo(participants.stream().map(participant -> participant.getUser().getEmail()).collect(toList())));


        //verify
        verify(roomRepository, times(1)).findOne(anyLong());
        verify(participantRepository, times(1)).findByRoom(any(Room.class));
    }


    @DisplayName("채팅방에 참여자 초대하기 테스트")
    @Test
    public void inviteParticipant() {

        //given
        User user = getUser();
        Room room = getRoom(user);

        List<User> users = mockUserFactory.createListAddedId();

        //when
//        participantService.inviteParticipants(room.getId(), );

        //then

        //verify
    }

}
