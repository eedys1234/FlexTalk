package com.flextalk.we.message.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.message.cmmn.MockMessageFactory;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.dto.MessageReadUpdateDto;
import com.flextalk.we.message.dto.MessageSaveRequestDto;
import com.flextalk.we.message.dto.MessageUnReadResponseDto;
import com.flextalk.we.message.service.MessageService;
import com.flextalk.we.participant.cmmn.ParticipantMatchers;
import com.flextalk.we.participant.domain.entity.Participant;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MessageControllerTest {

    @InjectMocks
    private MessageController messageController;

    @Mock
    private MessageService messageService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName(value = "메시지(TEXT) 보내기 테스트")
    @Test
    public void sendTextMessageTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/1/messages/text";

        String messageContent = "테스트 메시지입니다.";
        String messageType = "TEXT";
        MessageSaveRequestDto messageSaveRequestDto = new MessageSaveRequestDto();
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageContent", messageContent);
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageType", messageType);

        long sendMessageId = 1L;
        doReturn(sendMessageId).when(messageService).sendTextMessage(anyLong(), anyLong(), any(MessageSaveRequestDto.class));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageSaveRequestDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        SuccessResponse<Long> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SuccessResponse<Long>>() {
        });

        Long result = response.getResult();
        assertThat(result, equalTo(sendMessageId));

        //verify
        verify(messageService, times(1)).sendTextMessage(anyLong(), anyLong(), any(MessageSaveRequestDto.class));
    }

    @DisplayName(value = "메시지(FILE) 보내기 테스트")
    @Test
    public void sendFileMessageTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/1/messages/file";

        String messageContent = "테스트 메시지입니다.";
        String messageType = "FILE";
        MessageSaveRequestDto messageSaveRequestDto = new MessageSaveRequestDto();
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageContent", messageContent);
        ReflectionTestUtils.setField(messageSaveRequestDto, "messageType", messageType);

        long sendMessageId = 1L;
        doReturn(sendMessageId).when(messageService).sendFileMessage(anyLong(), anyLong(), any(MessageSaveRequestDto.class), any(String.class),
                any());

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "Hello, World!".getBytes()
        );

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                .file(mockMultipartFile)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageSaveRequestDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        SuccessResponse<Long> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SuccessResponse<Long>>() {
        });

        Long result = response.getResult();
        assertThat(result, equalTo(sendMessageId));

        //verify
        verify(messageService, times(1)).sendFileMessage(anyLong(), anyLong(), any(MessageSaveRequestDto.class),
                any(String.class), any());
    }

    @DisplayName(value = "메시지 삭제 테스트")
    @Test
    public void deleteMessageTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/participants/1/messages/1";

        long deleteMessageId = 1L;
        doReturn(deleteMessageId).when(messageService).deleteMessage(anyLong(), anyLong(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        SuccessResponse<Long> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SuccessResponse<Long>>() {
        });

        Long result = response.getResult();
        assertThat(result, equalTo(deleteMessageId));

        //verify
        verify(messageService, times(1)).deleteMessage(anyLong(), anyLong(), anyLong());
    }

    @DisplayName(value = "안읽은 수 조회 테스트")
    @Test
    public void unReadMessageCountTest() throws Exception {

        //given
        final String url = "/api/v1/rooms/1/unread-messages";
        MockUserFactory mockUserFactory = new MockUserFactory();
        User roomCreator = mockUserFactory.createAddedId(1L);
        List<User> users = Arrays.asList(mockUserFactory.createAddedId(2L), mockUserFactory.createAddedId(3L), mockUserFactory.createAddedId(4L));

        MockRoomFactory mockRoomFactory = new MockRoomFactory(roomCreator);
        String roomName = "테스트 채팅방";
        String roomType = "GROUP";
        int roomLimitCount = 10;
        long roomId = 1L;
        Room room = mockRoomFactory.create(roomName, roomType, roomLimitCount);
        ReflectionTestUtils.setField(room, "id", roomId);
        room.invite(users);

        long participantId = 1L;
        Participant roomOwnerParticipant = ParticipantMatchers.matchingRoomOwner(room);
        ReflectionTestUtils.setField(roomOwnerParticipant, "id", participantId);

        MockMessageFactory mockMessageFactory = new MockMessageFactory(room, roomOwnerParticipant);
        List<Message> messages = mockMessageFactory.createTextListAddedId();

        long unReadCount = 1L;
        List<MessageUnReadResponseDto> unReadMessagesCount = messages.stream()
                .map(message -> new MessageUnReadResponseDto(message.getId(), unReadCount))
                .collect(toList());

        doReturn(unReadMessagesCount).when(messageService).unReadMessagesCount(anyLong(), any(String.class));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("messageIds", messages.stream().map(message -> String.valueOf(message.getId())).collect(Collectors.joining(","))));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SuccessResponse<List<MessageUnReadResponseDto>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SuccessResponse<List<MessageUnReadResponseDto>>>() {
        });

        List<MessageUnReadResponseDto> result = response.getResult();

        assertThat(result.size(), equalTo(unReadMessagesCount.size()));
        assertThat(result, equalTo(unReadMessagesCount));

        //verify
        verify(messageService, times(1)).unReadMessagesCount(anyLong(), any(String.class));
    }

    @DisplayName(value = "메시지 읽기 테스트")
    @Test
    public void readMessageTest() throws Exception {

        //given
        final String url = "/api/v1/messages/read";
        long participantId = 1L;

        List<String> messageIds = new ArrayList<>();
        final long START_MESSAGE_ID = 10000000000L;
        final long END_MESSAGE_ID = 10000005000L;

        for(long i = START_MESSAGE_ID; i<END_MESSAGE_ID; i++) {
            messageIds.add(String.valueOf(i));
        }

        long isRead = 1L;
        long isSuccess = 1L;

        MessageReadUpdateDto messageReadUpdateDto = new MessageReadUpdateDto();
        ReflectionTestUtils.setField(messageReadUpdateDto, "participantId", participantId);
        ReflectionTestUtils.setField(messageReadUpdateDto, "messageIds", String.join(",", messageIds));

        doReturn(isRead).when(messageService).readMessage(any());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageReadUpdateDto))
        );

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        SuccessResponse<Long> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SuccessResponse<Long>>() {
        });

        Long result = response.getResult();
        assertThat(result, equalTo(isSuccess));

        //verify
        verify(messageService, times(1)).readMessage(any());

    }

}
