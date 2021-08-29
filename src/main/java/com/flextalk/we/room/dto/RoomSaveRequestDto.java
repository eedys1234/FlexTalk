package com.flextalk.we.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class RoomSaveRequestDto {

    @NotBlank
    @JsonProperty(value = "room_name")
    private String roomName;

    @NotBlank
    @JsonProperty(value = "room_type")
    private String roomType;

    @JsonProperty(value = "room_limit_count")
    private Integer roomLimitCount;

    public Room toEntity(User user) {
        return Room.create(user, this.roomName, this.roomType, this.roomLimitCount);
    }
}
