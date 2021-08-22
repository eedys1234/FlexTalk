package com.flextalk.we.room.cmmn;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockRoomCollection {

    private String[] roomNames = {
            "TEST 채팅방1",
            "TEST 채팅방2",
            "TEST 채팅방3",
            "TEST 채팅방4",
            "TEST 채팅방5",
            "TEST 채팅방6",
            "TEST 채팅방7",
            "TEST 채팅방8",
            "TEST 채팅방9",
            "TEST 채팅방10"
    };

    private String[] roomTypes = {
            "NORMAL",
            "NORMAL",
            "NORMAL",
            "GROUP",
            "GROUP",
            "GROUP",
            "OPEN",
            "OPEN",
            "OPEN",
            "NORMAL",
            "NORMAL",
    };

    public List<Room> create(User user) {
        List<Room> rooms = new ArrayList<>();

        for(int i=0;i<roomNames.length;i++) {
            MockRoom mockRoom = new MockRoom();
            rooms.add(mockRoom.create(user, roomNames[i], roomTypes[i], 2));
        }

        return rooms;
    }

}
