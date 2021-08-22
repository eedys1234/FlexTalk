package com.flextalk.we.room.cmmn;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;

public class MockRoom {

    public Room create(User user, String roomName, String roomType, int roomLimitCount) {
        Room room = Room.create(user, roomName, roomType, roomLimitCount);
        return room;
    }

}
