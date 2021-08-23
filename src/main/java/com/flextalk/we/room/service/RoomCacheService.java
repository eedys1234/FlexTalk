package com.flextalk.we.room.service;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomCacheService {

    private final RoomRepository roomRepository;

    /**
     * Cache Service getRooms 메서드롤 호출하는 호출자는 선언적 트랜잭션으로 감싸있을 것이라 예상됨
     * Spring Transactional 속성 중 Propagation default 전략은 PROPAGATION_REQUIRED
     * PROPAGATION_REQUIRED 전략은 기존 트랜잭션에 참여, 즉 호출자의 트랜잭션에 참여함
     * @param user
     * @return
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "rooms", key = "#user.id")
    public List<Room> getRooms(User user) {
        return roomRepository.findByUserId(user);
    }
}
