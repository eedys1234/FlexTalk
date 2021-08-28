package com.flextalk.we.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomTypeInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.fastinfoset.algorithm.BooleanEncodingAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Boolean isOwner;

    public RoomResponseDto(Long roomId, String roomName, RoomTypeInfo.RoomType roomType, Integer roomLimitCount, Boolean isAlarm, Boolean isBookMark, Boolean isOwner) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType.getKey();
        this.roomLimitCount = roomLimitCount;
        this.isAlarm = isAlarm;
        this.isBookMark = isBookMark;
        this.isOwner = isOwner;
    }

}
