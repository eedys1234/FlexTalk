package com.flextalk.we.message.controller;

import com.flextalk.we.message.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageControllerTest {

    @InjectMocks
    private MessageController messageController;

    @Mock
    private MessageService messageService;


    @DisplayName(value = "텍스트 메시지 보내기 ")
    @Test
    public void sendTextMessageTest() throws Exception {

        //given
        //when
        //then
        //verify
    }

    @DisplayName(value = "파일 메시지 보내기")
    @Test
    public void sendFileMessageTest() throws Exception {

        //given
        //when
        //then
        //verify
    }

    @DisplayName(value = "")
    @Test
    public void unReadMessageCountTest() throws Exception {

    }

}
