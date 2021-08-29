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

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", length = 30, nullable = false)
    private RoomType roomType;

    @Column(name = "room_limit_count", length = 4, nullable = false)
    private Integer roomLimitCount;

    public RoomTypeInfo(String roomType, Integer roomLimitCount) {
        this.roomType = RoomType.valueOf(Objects.requireNonNull(roomType.toUpperCase()));
        this.roomLimitCount = Objects.isNull(roomLimitCount) ? this.roomType.getDefault_participant_count() : roomLimitCount;

        validateCreateRoom(this.roomType, this.roomLimitCount);
    }

    private void validateCreateRoom(RoomType roomType, Integer roomLimitCount) {
        if(!canCreateRoom(roomType, roomLimitCount)) {
            throw new IllegalArgumentException(
                    String.format("참여인원이 적절하지 않습니다. roomParticipantCount : %s, roomType : %s", roomLimitCount, roomType)
            );
        }
    }

    private boolean canCreateRoom(RoomType roomType, Integer roomLimitCount) {

        switch (roomType) {
            case NORMAL : return roomLimitCount <= RoomType.NORMAL.getDefault_participant_count();
            case GROUP : return roomLimitCount <= RoomType.GROUP.getDefault_participant_count();
            case OPEN : return roomLimitCount <= RoomType.OPEN.getDefault_participant_count();
            default :
                throw new IllegalArgumentException("Unknown Room Type : " + roomType);
        }
    }

    @Getter
    public enum RoomType {
        NORMAL(2),
        GROUP(1000),
        OPEN(1000);

        private int default_participant_count;

        RoomType(final int default_participant_count) {
            this.default_participant_count = default_participant_count;
        }

        public String getKey() {
            return name();
        }
    }

}
