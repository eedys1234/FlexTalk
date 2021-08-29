package com.flextalk.we.room.controller;

import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.room.dto.RoomSaveRequestDto;
import com.flextalk.we.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class RoomController {

    private final RoomService roomService;

    @PostMapping(value = "/v1/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> createRoom(@RequestHeader("userId") Long userId,
                                            @RequestBody @Valid RoomSaveRequestDto roomSaveRequestDto) {

        Long roomId = roomService.createRoom(userId, roomSaveRequestDto);
        return SuccessResponse.of(HttpStatus.CREATED.value(), roomId);
    }

    @DeleteMapping(value = "/v1/rooms/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Long> deleteRoom(@RequestHeader("userId") Long userId,
                                            @PathVariable("roomId") Long roomId) {

        return SuccessResponse.of(HttpStatus.NO_CONTENT.value(), roomService.deleteRoom(userId, roomId));
    }

    @GetMapping(value = "/v1/rooms")
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<List<RoomResponseDto>> getRooms(@RequestHeader("userId") Long userId) {

        return SuccessResponse.of(HttpStatus.OK.value(), roomService.getRooms(userId));
    }


}
