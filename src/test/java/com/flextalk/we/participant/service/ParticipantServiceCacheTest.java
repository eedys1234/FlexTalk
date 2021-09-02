package com.flextalk.we.participant.service;

import com.flextalk.we.cmmn.configure.CacheConfiguration;
import com.flextalk.we.participant.cmmn.ParticipantMatchers;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.participant.domain.repository.ParticipantRepository;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.room.service.RoomService;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import({CacheConfiguration.class, ParticipantService.class})
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
}
