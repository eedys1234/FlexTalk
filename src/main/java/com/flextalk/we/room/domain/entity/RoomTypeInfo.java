package com.flextalk.we.room.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

/**
 * 값 타입
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class RoomTypeInfo {

    @Transient
    private final int MAX_PARTICIPANT_COUNT = 1000;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", length = 30, nullable = false)
    private RoomType roomType;

    @Column(name = "room_participant_count", length = 4, nullable = false)
    private Integer roomParticipantCount;

    public RoomTypeInfo(String roomType, Integer roomParticipantCount) {
        this.roomType = RoomType.valueOf(Objects.requireNonNull(roomType));
        this.roomParticipantCount = Objects.requireNonNull(roomParticipantCount);

        if(validate(this.roomType, roomParticipantCount)) {
            throw new IllegalArgumentException(
                    String.format("참여인원이 적절하지 않습니다. roomParticipantCount : %s, roomType : %s", roomParticipantCount, roomType)
            );
        }
    }

    private boolean validate(RoomType roomType, Integer roomParticipantCount) {
        if(roomType == RoomType.NORMAL && roomParticipantCount > 1) {
            return true;
        }
        else if(roomParticipantCount > MAX_PARTICIPANT_COUNT) {
            return true;
        }

        return false;
    }

    protected enum RoomType {
        NORMAL,
        GROUP,
        OPEN
    }

}
