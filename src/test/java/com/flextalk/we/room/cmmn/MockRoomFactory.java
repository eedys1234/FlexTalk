package com.flextalk.we.room.cmmn;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.*;

public class MockRoomFactory {

    private User user;
    private String[][] roomInfo = {
        {"TEST 채팅방1", "NORMAL", "2"},
        {"TEST 채팅방2", "NORMAL", "2"},
        {"TEST 채팅방3", "NORMAL", "2"},
        {"TEST 채팅방4", "GROUP", "2"},
        {"TEST 채팅방5", "GROUP", "2"},
        {"TEST 채팅방6", "GROUP", "2"},
        {"TEST 채팅방7", "OPEN", "2"},
        {"TEST 채팅방8", "OPEN", "2"},
        {"TEST 채팅방9", "OPEN", "2"},
        {"TEST 채팅방10", "NORMAL", "2"},
    };

    public MockRoomFactory(User user) {
        this.user = user;
    }

    public Room create(String roomName, String roomType, int roomLimitCount) {
        Room room = Room.create(user, roomName, roomType, roomLimitCount);
        return room;
    }

    public List<Room> createCollection() {
        return Arrays.stream(roomInfo)
                .map(info -> create(info[0], info[1], Integer.parseInt(info[2])))
                .collect(toList());
    }
}
