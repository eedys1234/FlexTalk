package com.flextalk.we.room.cmmn;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomMessageDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MockRoomMessageDateCollection {

    public List<RoomMessageDate> create(List<Room> rooms, LocalDateTime now, Function<LocalDateTime, LocalDateTime> func) {

        List<RoomMessageDate> roomMessageDates = new ArrayList<>();

        LocalDateTime apply = now;
        for(Room room : rooms) {
            RoomMessageDate roomMessageDate = RoomMessageDate.generate(room);
            roomMessageDate.update(apply);
            roomMessageDates.add(roomMessageDate);
            apply = func.apply(apply);
        }

        return roomMessageDates;
    }
}
