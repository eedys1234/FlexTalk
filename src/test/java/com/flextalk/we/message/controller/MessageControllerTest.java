package com.flextalk.we.message.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.message.dto.MessageReadUpdateDto;
import com.flextalk.we.message.service.MessageService;
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
import java.util.List;

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

        //when

        //then

        //verify

    }

    @DisplayName(value = "메시지(FILE) 보내기 테스트")
    @Test
    public void sendFileMessageTest() throws Exception {

        //given

        //when

        //then

        //verify

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

        //when

        //then

        //verify

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
