package com.flextalk.we.participant.service;

import com.flextalk.we.cmmn.configure.CacheMakerConfiguration;
import com.flextalk.we.cmmn.util.CacheNames;
import com.flextalk.we.participant.cmmn.ParticipantMatchers;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.participant.domain.repository.ParticipantRepository;
import com.flextalk.we.participant.dto.ParticipantPromoteRequestDto;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.service.RoomService;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import({CacheMakerConfiguration.class, ParticipantService.class})
@ExtendWith(SpringExtension.class)
@EnableCaching
@ImportAutoConfiguration(classes = {
    CacheAutoConfiguration.class,
    RedisAutoConfiguration.class
})
public class ParticipantServiceCacheTest {

    @MockBean
    private UserService mockUserService;

    @MockBean
    private RoomService mockRoomService;

    @MockBean
    private ParticipantRepository mockParticipantRepository;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    public void after() {
        Objects.requireNonNull(cacheManager.getCache(CacheNames.PARTICIPANTS)).clear();
    }

    @DisplayName("채팅방의 참여자 리스트 조회(캐시)")
    @Test
    public void givenCachingGetParticipantsByRoomTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        //when
        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(cacheMissParticipants.size(), equalTo(participants.size()));
        assertThat(cacheMissParticipants, equalTo(participants.stream().map(ParticipantResponseDto::new).collect(toList())));

        assertThat(cacheHitParticipants.size(), equalTo(participants.size()));
        assertThat(cacheHitParticipants, equalTo(participants.stream().map(ParticipantResponseDto::new).collect(toList())));

        //verify
        verify(mockRoomService, times(1)).findRoom(anyLong());
        verify(mockParticipantRepository, times(1)).findByRoom(any(Room.class));
    }

    @DisplayName("채팅방 참여자 초대 시 캐시정보 삭제 테스트")
    @Test
    public void cacheEvictInviteParticipantsTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        User invitedUser = mockUserFactory.createAddedId(5L);

        doReturn(Collections.singletonList(invitedUser)).when(mockUserService).findUsers(any());
        doReturn(room).when(mockRoomService).findRoomAddedAddiction(anyLong());

        //when
        Long invitedRoomId = participantService.inviteParticipants(room.getId(), String.valueOf(invitedUser.getId()));
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(invitedRoomId, equalTo(roomId));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));
    }
    
    @DisplayName("채팅방 사용자가 나갈 시 캐시정보 삭제 테스트")
    @Test
    public void evictCacheLeaveParticipantTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        long participantId = 1L;
        Participant participant = ParticipantMatchers.matchingParticipant(room, users.get(0));
        ReflectionTestUtils.setField(participant, "id", participantId);

        doReturn(Optional.of(participant)).when(mockParticipantRepository).findOne(anyLong());
        doReturn(room).when(mockRoomService).findRoomAddedAddiction(anyLong());

        //when
        Long leaveRoomId = participantService.leaveParticipant(room.getId(), participant.getId());
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(leaveRoomId, equalTo(room.getId()));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));
    }

    @DisplayName("채팅방 참여자 추방시키기 캐시정보 삭제")
    @Test
    public void evictCacheDeportParticipantsTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        long roomOwnerParticipantId = 1L;
        long deportParticipantId = 2L;

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", roomOwnerParticipantId);

        Participant participant = ParticipantMatchers.matchingParticipant(room, users.get(0));
        ReflectionTestUtils.setField(participant, "id", deportParticipantId);

        doReturn(Collections.singletonList(participant)).when(mockParticipantRepository).findByIds(any());
        doReturn(Optional.of(roomOwnerParticipant)).when(mockParticipantRepository).findOwner(anyLong());
        doReturn(room).when(mockRoomService).findRoomAddedAddiction(anyLong());

        //when
        Long deportRoomId = participantService.deportParticipants(room.getId(), roomOwnerParticipant.getId(), String.valueOf(participant.getId()));
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(deportRoomId, equalTo(room.getId()));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));
    }

    @DisplayName("채팅방 권한 양도하기 캐시정보 삭제 테스트")
    @Test
    public void evictCachePromotePermissionTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        long roomOwnerParticipantId = 1L;
        long promoteParticipantId = 2L;

        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", roomOwnerParticipantId);

        Participant participant = ParticipantMatchers.matchingParticipant(room, users.get(0));
        ReflectionTestUtils.setField(participant, "id", promoteParticipantId);

        ParticipantPromoteRequestDto participantPromoteRequestDto = new ParticipantPromoteRequestDto();
        ReflectionTestUtils.setField(participantPromoteRequestDto, "promoteParticipantId", promoteParticipantId);

        doReturn(Optional.of(roomOwnerParticipant)).when(mockParticipantRepository).findOwner(anyLong());
        doReturn(Optional.of(participant)).when(mockParticipantRepository).findOne(anyLong());

        //when
        Long promoteRoomId = participantService.promotePermission(room.getId(), roomOwnerParticipant.getId(), participantPromoteRequestDto);
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(promoteRoomId, equalTo(room.getId()));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));
    }

    @DisplayName("즐겨찾기 등록 캐시정보 삭제 테스트")
    @Test
    public void evictCacheAddBookMarkToParticipantTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        long participantId = 1L;

        Participant participant = ParticipantMatchers.matchingParticipant(room, users.get(0));
        ReflectionTestUtils.setField(participant, "id", participantId);

        doReturn(Optional.of(participant)).when(mockParticipantRepository).findOne(anyLong());
        doReturn(room).when(mockRoomService).findRoomAddedAddiction(anyLong());

        //when
        Long addRoomId = participantService.addBookMarkToParticipant(participant.getId(), room.getId());
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(addRoomId, equalTo(room.getId()));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));
    }

    @DisplayName("즐겨찾기 삭제 캐시정보 삭제 테스트")
    @Test
    public void evictCacheDeleteBookMarkToParticipantTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        long participantId = 1L;

        Participant participant = ParticipantMatchers.matchingParticipant(room, users.get(0));
        ReflectionTestUtils.setField(participant, "id", participantId);

        doReturn(Optional.of(participant)).when(mockParticipantRepository).findOne(anyLong());
        doReturn(room).when(mockRoomService).findRoomAddedAddiction(anyLong());

        room.addBookMark(participant);

        //when
        Long deleteRoomId = participantService.deleteBookMarkToParticipant(participant.getId(), room.getId());
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(deleteRoomId, equalTo(room.getId()));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));
    }

    @DisplayName("알람 추가 캐시정보 삭제 테스트")
    @Test
    public void evictCacheAddAlarmToParticipantTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        long participantId = 1L;

        Participant participant = ParticipantMatchers.matchingParticipant(room, users.get(0));
        ReflectionTestUtils.setField(participant, "id", participantId);

        doReturn(Optional.of(participant)).when(mockParticipantRepository).findOne(anyLong());
        doReturn(room).when(mockRoomService).findRoomAddedAddiction(anyLong());

        room.deleteAlarm(participant);

        //when
        Long addRoomId = participantService.addAlarmToParticipant(participant.getId(), room.getId());
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(addRoomId, equalTo(room.getId()));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));

    }

    @DisplayName("알람 삭제 캐시정보 삭제 테스트")
    @Test
    public void evictCacheDeleteAlarmToParticipantTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "채팅방 테스트";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);

        List<Participant> participants = room.participants();

        room.invite(users);
        doReturn(room).when(mockRoomService).findRoom(anyLong());
        doReturn(participants).when(mockParticipantRepository).findByRoom(any(Room.class));

        List<ParticipantResponseDto> cacheMissParticipants = participantService.getParticipantsByRoom(room.getId());
        List<ParticipantResponseDto> cacheHitParticipants = participantService.getParticipantsByRoom(room.getId());

        long participantId = 1L;

        Participant participant = ParticipantMatchers.matchingParticipant(room, users.get(0));
        ReflectionTestUtils.setField(participant, "id", participantId);

        doReturn(Optional.of(participant)).when(mockParticipantRepository).findOne(anyLong());
        doReturn(room).when(mockRoomService).findRoomAddedAddiction(anyLong());

        //when
        Long deleteRoomId = participantService.deleteAlarmToParticipant(participant.getId(), room.getId());
        List<ParticipantResponseDto> twiceCacheMissParticipants = participantService.getParticipantsByRoom(room.getId());

        //then
        assertThat(deleteRoomId, equalTo(room.getId()));

        //verify
        verify(mockRoomService, times(2)).findRoom(anyLong());
        verify(mockParticipantRepository, times(2)).findByRoom(any(Room.class));
    }


}
