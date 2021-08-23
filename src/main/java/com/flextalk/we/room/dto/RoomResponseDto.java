package com.flextalk.we.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomTypeInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.fastinfoset.algorithm.BooleanEncodingAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomResponseDto {

    @JsonProperty(value = "room_id")
    private Long roomId;

    @JsonProperty(value = "room_name")
    private String roomName;

    @JsonProperty(value = "room_type")
    private String roomType;

    @JsonProperty(value = "room_limit_count")
    private Integer roomLimitCount;

    private Boolean isAlarm;

    private Boolean isBookMark;

    public RoomResponseDto(Room room) {
        this.roomId = room.getId();
        this.roomName = room.getRoomName();
        this.roomType = room.getRoomTypeInfo().getRoomType().getKey();
        this.roomLimitCount = room.getRoomTypeInfo().getRoomLimitCount();
        this.isAlarm = room.getRoomAlarms().isEmpty() ? Boolean.FALSE : Boolean.TRUE;
        this.isBookMark = room.getRoomBookMarks().isEmpty() ? Boolean.FALSE : Boolean.TRUE;
    }

}
