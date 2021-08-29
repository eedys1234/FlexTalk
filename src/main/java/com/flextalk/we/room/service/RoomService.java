package com.flextalk.we.room.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.cmmn.util.CacheNames;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.room.dto.RoomSaveRequestDto;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 채팅방에 관한 관리 클래스
 */
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserService userService;

    /**
     * 채팅방 생성 함수
     * @param roomSaveRequestDto 채팅방 생성 시 필요한 정보를 전달하는 Dto
     * @return Room Id
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자)
     */
    @CacheEvict(cacheNames = CacheNames.ROOMS, key = "#userId")
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
    @CacheEvict(cacheNames = CacheNames.ROOMS, key = "#userId")
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
     * Cache Service getRooms 메서드롤 호출하는 호출자는 선언적 트랜잭션으로 감싸있을 것이라 예상됨
     * Spring Transactional 속성 중 Propagation default 전략은 PROPAGATION_REQUIRED
     * PROPAGATION_REQUIRED 전략은 기존 트랜잭션에 참여, 즉 호출자의 트랜잭션에 참여함
     * 사용자의 Rooms
     * @param userId 사용자 ID
     * @return Room 리스트
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(사용자)
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ROOMS, key = "#userId")
    public List<RoomResponseDto> getRooms(Long userId) {

        final User user = userService.findUser(userId);
        return roomRepository.findByUser(user);
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
