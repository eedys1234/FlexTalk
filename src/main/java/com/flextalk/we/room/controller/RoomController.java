package com.flextalk.we.room.controller;

import com.flextalk.we.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;



}
