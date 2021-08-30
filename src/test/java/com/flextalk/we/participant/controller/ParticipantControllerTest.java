package com.flextalk.we.participant.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.participant.cmmn.MockParticipantFactory;
import com.flextalk.we.participant.dto.ParticipantPromoteRequestDto;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.participant.dto.ParticipantSaveRequestDto;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.service.ParticipantService;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO : 반환값에 대한 검증이 디테일하게 이루어져야함 즉, jsonValue 값 확인 필요
@ExtendWith(MockitoExtension.class)
public class ParticipantControllerTest {

    @InjectMocks
    private ParticipantController participantController;

    @Mock
    private ParticipantService participantService;


    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(participantController).build();
        this.objectMapper = new ObjectMapper();
    }
    
    @DisplayName("채팅방 내 참여자 리스트 가져오기 테스트")
    @Test
    public void getParticipantsTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants";
        MockUserFactory mockUserFactory = new MockUserFactory();
        long userId = 1L;
        User roomCreator = mockUserFactory.createAddedId(userId);

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트 채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 100;

        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        long roomId = 1L;
        ReflectionTestUtils.setField(room, "id", roomId);

        MockParticipantFactory mockParticipantFactory = new MockParticipantFactory(room);
        List<Participant> participants = mockParticipantFactory.createList(
                Arrays.asList(
                        mockUserFactory.createAddedId(2L),
                        mockUserFactory.createAddedId(3L),
                        mockUserFactory.createAddedId(4L),
                        mockUserFactory.createAddedId(5L)
                ));

        doReturn(participants.stream()
        .map(ParticipantResponseDto::new)
        .collect(toList())).when(participantService).getParticipantsByRoom(anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SuccessResponse<List<ParticipantResponseDto>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SuccessResponse<List<ParticipantResponseDto>>>() {

        });

        List<ParticipantResponseDto> result = response.getResult();

        //verify
        verify(participantService, times(1)).getParticipantsByRoom(anyLong());
    }

    @DisplayName("여러 명의 사용자 초대 테스트")
    @Test
    public void inviteParticipantsTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants";
        final long userId = 1L;
        final int START_PARTICIPANTS = 100000;
        final int MAX_PARTICIPANTS = START_PARTICIPANTS + 1000;

        List<String> tempUserIds = new ArrayList<>();
        for(int i = START_PARTICIPANTS; i<= MAX_PARTICIPANTS; i++) {
            tempUserIds.add(String.valueOf(i));
        }

        ParticipantSaveRequestDto participantSaveRequestDto = new ParticipantSaveRequestDto();
        ReflectionTestUtils.setField(participantSaveRequestDto, "userIds", tempUserIds.stream().collect(joining(",")));
        final long roomId = 3L;

        doReturn(roomId).when(participantService).inviteParticipants(anyLong(), any(String.class));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participantSaveRequestDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        SuccessResponse<Long> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SuccessResponse<Long>>() {
        });

        //verify
        verify(participantService, times(1)).inviteParticipants(anyLong(), any(String.class));
    }

    @DisplayName("채팅방 나가기 테스트")
    @Test
    public void leaveParticipantTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/2";
        final long leaveParticipantId = 2L;
        doReturn(leaveParticipantId).when(participantService).leaveParticipant(anyLong(), anyLong());

        final long userId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        //verify
        verify(participantService, times(1)).leaveParticipant(anyLong(), anyLong());
    }

    @DisplayName("채팅방 추방하기")
    @Test
    public void deportParticipantsTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/owner-participant/1";
        final long roomId = 1L;

        final int START_PARTICIPANTS = 10000000;
        final int MAX_PARTICIPANTS = START_PARTICIPANTS + 1000;

        List<String> tempParticipantIds = new ArrayList<>();
        for(int i = START_PARTICIPANTS; i<= MAX_PARTICIPANTS; i++) {
            tempParticipantIds.add(String.valueOf(i));
        }

        doReturn(roomId).when(participantService).deportParticipants(anyLong(), anyLong(), any(String.class));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("deportParticipantIds", String.join(",", tempParticipantIds)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        //verify
        verify(participantService).deportParticipants(anyLong(), anyLong(), any(String.class));
    }

    @DisplayName("채팅방 권한 전달하기 테스트")
    @Test
    public void promotePermissionTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/owner-participant/1";
        final long roomId = 1L;

        doReturn(roomId).when(participantService).promotePermission(anyLong(), anyLong(), any());

        ParticipantPromoteRequestDto participantPromoteRequestDto = new ParticipantPromoteRequestDto();
        ReflectionTestUtils.setField(participantPromoteRequestDto, "promoteParticipantId", 2L);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participantPromoteRequestDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(participantService, times(1)).promotePermission(anyLong(), anyLong(), any());
    }

    @DisplayName("즐겨찾기 등록 테스트")
    @Test
    public void addBookMarkToParticipantTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/1/bookmark";
        final long roomId = 1L;

        doReturn(roomId).when(participantService).addBookMarkToParticipant(anyLong(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(participantService, times(1)).addBookMarkToParticipant(anyLong(), anyLong());
    }

    @DisplayName("즐겨찾기 삭제 테스트")
    @Test
    public void deleteMarkToParticipantTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/1/bookmark";
        final long roomId = 1L;

        doReturn(roomId).when(participantService).deleteBookMarkToParticipant(anyLong(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        //verify
        verify(participantService, times(1)).deleteBookMarkToParticipant(anyLong(), anyLong());
    }

    @DisplayName("설정 등록 테스트")
    @Test
    public void addAlarmToParticipantTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/1/alarm";
        final long roomId = 1L;

        doReturn(roomId).when(participantService).addAlarmToParticipant(anyLong(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(participantService, times(1)).addAlarmToParticipant(anyLong(), anyLong());
    }


    @DisplayName("설정 삭제 테스트")
    @Test
    public void deleteAlarmToParticipantTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/1/alarm";
        final long roomId = 1L;

        doReturn(roomId).when(participantService).deleteAlarmToParticipant(anyLong(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        //verify
        verify(participantService, times(1)).deleteAlarmToParticipant(anyLong(), anyLong());

    }
}
