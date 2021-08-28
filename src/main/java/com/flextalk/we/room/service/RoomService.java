package com.flextalk.we.room.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.room.dto.RoomSaveRequestDto;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import com.flextalk.we.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 채팅방에 관한 관리 클래스
 */
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
//    private final UserRepository userRepository;
    private final UserService userService;
    private final RoomCacheService roomCacheService;


    /**
     * 채팅방 생성 함수
     * @param roomSaveRequestDto 채팅방 생성 시 필요한 정보를 전달하는 Dto
     * @return Room Id
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자)
     */
    @Transactional
    public Long createRoom(Long userId, RoomSaveRequestDto roomSaveRequestDto) {

        final User user = userService.findUser(userId);

        return roomRepository.save(roomSaveRequestDto.toEntity(user)).getId();
    }

    /**
     * 채팅방 삭제 함수
     * @param userId 채팅방 Owner
     * @param roomId 채팅방 ID
     * @return 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자 | 채팅방)
     */
    @Transactional
    public Long deleteRoom(Long userId, Long roomId) {

        final User user = userService.findUser(userId);

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
     * @param userId 사용자 ID
     * @return Rooms
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자)
     */
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getRooms(Long userId) {

        final User user = userService.findUser(userId);

        return roomCacheService.getRooms(user);
    }

    @Transactional(readOnly = true)
    public Room findRoom(final Long roomId) {
        return roomRepository.findOne(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));
    }

    @Transactional(readOnly = true)
    public Room findRoomAddedAddiction(final Long roomId) {
        return roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));

    }
}
