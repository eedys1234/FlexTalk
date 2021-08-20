package com.flextalk.we.room.cmmn;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomBookMark;
import com.flextalk.we.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;

public class MockRoomBookMarkCollection {

    public List<RoomBookMark> create(User user, List<Room> rooms) {

        List<RoomBookMark> bookMarks = new ArrayList<>();

        for(Room room : rooms) {
            bookMarks.add(RoomBookMark.of(user, room));
        }

        return bookMarks;
    }
}
