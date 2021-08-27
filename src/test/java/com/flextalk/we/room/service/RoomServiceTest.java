package com.flextalk.we.room.service;

import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.room.dto.RoomSaveRequestDto;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import com.flextalk.we.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomCacheService roomCacheService;

    private MockUserFactory mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new MockUserFactory();
    }

    private User getUser() {
        Long userId = 1L;
        User user = mockUser.create();
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    private Room getRoom(User user) {
        MockRoomFactory mockRoomFactory = new MockRoomFactory(user);
        String roomName = "테스트 채팅방";
        String roomType = "NORMAL";
        int roomLimitCount = 2;
        return mockRoomFactory.create(roomName, roomType, roomLimitCount);
    }

    @DisplayName("채팅방 생성 테스트")
    @Test
    public void createRoomTest() {

        //given
        User user = getUser();
        String roomName = "테스트 채팅방";
        String roomType = "NORMAL";
        Integer roomLimitCount = 2;

        RoomSaveRequestDto roomSaveRequestDto = new RoomSaveRequestDto();
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomName", roomName);
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomType", roomType);
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomLimitCount", roomLimitCount);
        Room room = roomSaveRequestDto.toEntity(user);

        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(room).when(roomRepository).save(any(Room.class));

        //when
        Long createdRoomId = roomService.createRoom(user.getId(), roomSaveRequestDto);

        //then
        assertThat(createdRoomId, is(room.getId()));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @DisplayName("채팅방 삭제 테스트")
    @Test
    public void deleteRoomTest() {

        //given
        User user = getUser();
        Room room = getRoom(user);

        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOne(anyLong());
        doReturn(room).when(roomRepository).save(any(Room.class));

        //when
        Long deleteRoomId = roomService.deleteRoom(user.getId(), room.getId());

        //then
        Room findRoom = roomRepository.findOne(roomId).orElse(null);
        assertThat(deleteRoomId, is(roomId));
        assertThat(findRoom.getIsDelete(), is(Boolean.TRUE));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(2)).findOne(anyLong());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @DisplayName("캐시된 사용자의 채팅방 리스트 가져오기 테스트")
    @Test
    public void getRooms() {

        //given
        User user = getUser();
        MockRoomFactory mockRoomFactory = new MockRoomFactory(user);
        List<Room> rooms = mockRoomFactory.createList();

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(rooms).when(roomCacheService).getRooms(any(User.class));

        //when
        List<RoomResponseDto> findRooms = roomService.getRooms(user.getId());

        //then
        assertThat(findRooms.size(), equalTo(rooms.size()));
        assertThat(findRooms.stream().map(RoomResponseDto::getRoomId).collect(toList()),
                equalTo(rooms.stream().map(Room::getId).collect(toList())));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomCacheService, times(1)).getRooms(any(User.class));
    }

    @DisplayName("채팅방에 즐겨찾기 등록 테스트")
    @Test
    public void addBookMarkToRoomTest() {

        //given
        User user = getUser();
        Room room = getRoom(user);
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        //when
        Long addBookMarkRoomId = roomService.addBookMarkToRoom(user.getId(), room.getId());

        //then
        assertThat(addBookMarkRoomId, is(room.getId()));
        assertThat(room.getRoomBookMarks(), not(empty()));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
    }

    @DisplayName("채팅방에 즐겨찾기를 등록한 사용자가 다시 즐겨찾기 등록할 경우 테스트")
    @Test
    public void alreadyBookMarkAddToRoomExceptionTest() {

        //given
        User user = getUser();
        Room room = spy(getRoom(user));
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        room.addBookMark(user);

        //when
        assertThrows(IllegalArgumentException.class, () -> roomService.addBookMarkToRoom(user.getId(), room.getId()));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
        verify(room, times(2)).addBookMark(any(User.class));
    }

    @DisplayName("채팅방에 즐겨찾기 삭제")
    @Test
    public void deleteBookMarkToRoomTest() {

        //given
        User user = getUser();
        Room room = getRoom(user);
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);
        room.addBookMark(user);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        //when
        Long deleteBookMarkRoomId = roomService.deleteBookMarkToRoom(user.getId(), room.getId());

        //then
        assertThat(deleteBookMarkRoomId, equalTo(room.getId()));
        assertThat(room.getRoomBookMarks(), empty());

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
    }
    
    @DisplayName("채팅방에 등록된 즐겨찾기가 없음에도 즐겨찾기를 삭제할 경우 테스트")
    @Test
    public void emptyBookMarkDeleteToRoomExceptionTest() {

        //given
        User user = getUser();
        Room room = spy(getRoom(user));
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        //when
        assertThrows(IllegalArgumentException.class, () -> roomService.deleteBookMarkToRoom(user.getId(), room.getId()));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
        verify(room, times(1)).deleteBookMark(any(User.class));
    }
    
    @DisplayName("채팅방에 알람 설정 기능 테스트")
    @Test
    public void addAlarmToRoomTest() {

        //given
        User user = getUser();
        Room room = getRoom(user);
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        //when
        Long addAlarmToRoomId = roomService.addAlarmToRoom(user.getId(), room.getId());

        //then
        assertThat(addAlarmToRoomId, equalTo(addAlarmToRoomId));
        assertThat(room.getRoomAlarms(), not(empty()));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
    }

    @DisplayName("채팅방에 설정된 알람이 이미 존재할 경우 테스트")
    @Test
    public void alreadyAlarmAddToRoomExceptionTest() {

        //given
        User user = getUser();
        Room room = spy(getRoom(user));
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        room.addAlarm(user);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        //when
        assertThrows(IllegalArgumentException.class, () -> roomService.addAlarmToRoom(user.getId(), room.getId()));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
        verify(room, times(2)).addAlarm(any(User.class));
    }

    @DisplayName("채팅방에 설정된 알람을 제거하는 기능 테스트")
    @Test
    public void deleteAlarmToRoom() {

        //given
        User user = getUser();
        Room room = getRoom(user);
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        room.addAlarm(user);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        //when
        Long deleteAlarmToRoomId = roomService.deleteAlarmToRoom(user.getId(), room.getId());

        //then
        assertThat(deleteAlarmToRoomId, equalTo(room.getId()));
        assertThat(room.getRoomAlarms(), empty());

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
    }

    @DisplayName("설정이 제거된 알람을 다시 제거하려할 때 테스트")
    @Test
    public void emptyAlarmDeleteToRoomExceptionTest() {

        //given
        User user = getUser();
        Room room = spy(getRoom(user));
        Long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        doReturn(user).when(userService).findUser(anyLong());
        doReturn(Optional.ofNullable(room)).when(roomRepository).findOneWithDetailInfo(anyLong());

        //when, then
        assertThrows(IllegalArgumentException.class, () -> roomService.deleteAlarmToRoom(user.getId(), room.getId()));

        //verify
        verify(userService, times(1)).findUser(anyLong());
        verify(roomRepository, times(1)).findOneWithDetailInfo(anyLong());
        verify(room, times(1)).deleteAlarm(any(User.class));
    }

}
