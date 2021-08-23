package com.flextalk.we.room.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.room.dto.RoomSaveRequestDto;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomCacheService roomCacheService;


    /**
     * 채팅방 생성 함수
     * @param roomSaveRequestDto 채팅방 생성 시 필요한 정보를 전달하는 Dto
     * @return Room Id
     */
    @Transactional
    public Long createRoom(Long userId, RoomSaveRequestDto roomSaveRequestDto) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        return roomRepository.save(roomSaveRequestDto.toEntity(user)).getId();
    }

    /**
     * 채팅방 삭제 함수
     * @param userId 채팅방 Owner
     * @param roomId 채팅방 Id
     * @return 채팅방 Id
     */
    @Transactional
    public Long deleteRoom(Long userId, Long roomId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        Room room = roomRepository.findOne(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));

        if(room.canDelete(user)) {
            room.delete(user);
            roomRepository.save(room);
        }

        return room.getId();
    }

    /**
     * 사용자의 Rooms
     * @param userId 사용자
     * @return Rooms
     */
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getRooms(Long userId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        List<Room> rooms = roomCacheService.getRooms(user);

        return rooms.stream()
                .map(RoomResponseDto::new)
                .collect(toList());
    }

    /**
     *
     * @param userId
     * @param roomId
     * @return
     */
    @Transactional
    public Long addBookMark(Long userId, Long roomId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));


        room.addBookMark(user);
        return room.getId();
    }

    /**
     *
     * @param userId
     * @param roomId
     * @return
     */
    @Transactional
    public Long setAlarm(Long userId, Long roomId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));


        room.setAlarm(user);
        return room.getId();
    }
}
