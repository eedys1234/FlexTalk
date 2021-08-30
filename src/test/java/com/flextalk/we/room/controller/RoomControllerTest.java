package com.flextalk.we.room.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.room.cmmn.MockRoomFactory;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.room.dto.RoomSaveRequestDto;
import com.flextalk.we.room.service.RoomService;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.User;
import org.assertj.core.api.Assertions;
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

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomService roomService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("채팅방 생성 테스트")
    @Test
    public void createRoomTest() throws Exception {

        //given
        final String url = "/api/v1/rooms";
        final Long userId = 1L;

        final RoomSaveRequestDto roomSaveRequestDto = new RoomSaveRequestDto();
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomName", "채팅방");
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomType", "NORMAL");
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomLimitCount", 2);

        doReturn(1L).when(roomService).createRoom(anyLong(), any());

        //when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(roomSaveRequestDto)));

        //then
        final MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(roomService, times(1)).createRoom(anyLong(), any());
    }

    @DisplayName("채팅방 생성 시 이름 누락 테스트")
    @Test
    public void createRoomEmptyRoomNameExceptionTest() throws Exception {

        //given
        final String url = "/api/v1/rooms";
        final Long userId = 1L;

        final RoomSaveRequestDto roomSaveRequestDto = new RoomSaveRequestDto();
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomType", "NORMAL");
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomLimitCount", 2);

        //when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(roomSaveRequestDto)));

        //then
        final MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @DisplayName("채팅방 생성 시 채팅방 타입 누락 테스트")
    @Test
    public void createRoomEmptyRoomTypeExceptionTest() throws Exception {

        //given
        final String url = "/api/v1/rooms";
        final Long userId = 1L;

        final RoomSaveRequestDto roomSaveRequestDto = new RoomSaveRequestDto();
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomName", "채팅방");
        ReflectionTestUtils.setField(roomSaveRequestDto, "roomLimitCount", 2);

        //when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(roomSaveRequestDto)));

        //then
        final MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @DisplayName("채팅방 삭제 테스트")
    @Test
    public void deleteRoomControllerTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1";
        doReturn(1L).when(roomService).deleteRoom(anyLong(), anyLong());

        //when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .header("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        final MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        //verify
        verify(roomService, times(1)).deleteRoom(anyLong(), anyLong());
    }

    @DisplayName("채팅방 리스트 가져오기 테스트")
    @Test
    public void getRoomsControllerTest() throws Exception {

        //given
        final String url = "/api/v1/rooms";
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.create();

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        List<Room> rooms = mockRoomFactory.createListAddedId();

        List<RoomResponseDto> expectedRooms = rooms.stream()
                .map(room -> new RoomResponseDto(room.getId(), room.getRoomName(), room.getRoomTypeInfo().getRoomType(),
                        room.getRoomTypeInfo().getRoomLimitCount(), false, false, false))
                .collect(toList());

        doReturn(expectedRooms).when(roomService).getRooms(anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SuccessResponse<List<RoomResponseDto>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<SuccessResponse<List<RoomResponseDto>>>() {
        });

        List<RoomResponseDto> result = response.getResult();
        assertThat(result.size(), equalTo(expectedRooms.size()));

        //verify
        verify(roomService, times(1)).getRooms(anyLong());
    }

}
