package com.flextalk.we.room.controller;

import com.flextalk.we.room.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomService roomService;

    @DisplayName("채팅방 생성 테스트")
    @Test
    public void createRoomControllerTest() {

        //given
        //when
        //then
        //verify
    }

    @DisplayName("채팅방 리스트 가져오기 테스트")
    @Test
    public void getRoomsControllerTest() {

        //given
        //when
        //then
        //verify
    }

}
