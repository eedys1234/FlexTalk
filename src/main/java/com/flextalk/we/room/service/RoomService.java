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

import static java.util.stream.Collectors.toList;

/**
 * 채팅방에 관한 관리 클래스
 */
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
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자)
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
     * @param roomId 채팅방 ID
     * @return 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자 | 채팅방)
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
     * @param userId 사용자 ID
     * @return Rooms
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자)
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
     * 즐겨찾기 등록
     * @param userId 즐겨찾기를 등록하려는 사용자 ID
     * @param roomId 즐겨찾기를 등록하려는 채팅방 ID
     * @return 즐겨찾기가 등록된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자 | 채팅방)
     * @throws IllegalStateException 채팅방에 참여자가 존재하지 않을경우
     * @throws IllegalArgumentException 이미 즐겨찾기가 등록되어있거나 채팅방의 참여자가 아닐경우 
     */
    @Transactional
    public Long addBookMarkToRoom(Long userId, Long roomId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));


        room.addBookMark(user);
        return room.getId();
    }

    /**
     * 즐겨찾기 삭제
     * @param userId 즐겨찾기를 삭제하려는 사용자 ID
     * @param roomId 즐겨찾기가 등록된 채팅방 ID
     * @return 즐겨찾기가 삭제된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자 | 채팅방)
     * @throws IllegalArgumentException 삭제하려는 채팅방에 삭제하려는 즐겨찾기가 없을경우
     */
    @Transactional
    public Long deleteBookMarkToRoom(Long userId, Long roomId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));


        room.deleteBookMark(user);
        return room.getId();
    }

    /**
     * 알람 설정
     * @param userId 알람을 설정하려는 사용자 ID
     * @param roomId 알람을 설정하려는 채팅방 ID
     * @return 알람이 설정된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자 | 채팅방)
     * @throws IllegalStateException 채팅방에 참여자가 존재하지 않을경우
     * @throws IllegalArgumentException 이미 알람이 설정되어있거나 채팅방의 참여자가 아닐경우
     */
    @Transactional
    public Long addAlarmToRoom(Long userId, Long roomId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));


        room.addAlarm(user);
        return room.getId();
    }

    /**
     * 알람 삭제
     * @param userId 알람을 삭제하려는 사용자 ID
     * @param roomId 알람이 설정된 채팅방 ID
     * @return 알람이 삭제된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자 | 채팅방)
     * @throws IllegalArgumentException 알람이 설정되지 않을경우
     */
    @Transactional
    public Long deleteAlarmToRoom(Long userId, Long roomId) {

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));


        room.deleteAlarm(user);
        return room.getId();
    }
}
