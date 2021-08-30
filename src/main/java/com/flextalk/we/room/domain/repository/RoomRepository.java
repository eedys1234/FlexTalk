package com.flextalk.we.room.domain.repository;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.dto.RoomResponseDto;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.flextalk.we.participant.domain.entity.QParticipant.*;
import static com.flextalk.we.room.domain.entity.QRoom.room;

@Repository
@RequiredArgsConstructor
public class RoomRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public Room save(Room room) {
        entityManager.persist(room);
        return room;
    }

    public List<RoomResponseDto> findByUser(User user) {
        return queryFactory.select(Projections.constructor(RoomResponseDto.class,
                    room.id, room.roomName, room.roomTypeInfo.roomType, room.roomTypeInfo.roomLimitCount,
                    participant.isAlarm, participant.isBookMark, participant.isOwner
                ))
                .from(room)
                .innerJoin(room.participants, participant)
                .where(participant.user.eq(user))
                .orderBy(room.id.asc())
                .fetch();
    }

    public Optional<Room> findOne(Long id) {
        return Optional.ofNullable(
            entityManager.find(Room.class, id)
        );
    }

    public Optional<Room> findOneWithDetailInfo(Long id) {
        return Optional.ofNullable(
                queryFactory.select(room)
                .from(room)
                .innerJoin(room.participants, participant)
                .fetchJoin()
                .where(room.id.eq(id))
                .fetchOne()
        );
    }

    public void clear() {
        entityManager.clear();
    }

    /**
     * 채팅방 삭제
     * @param room 채팅방
     * @return 성공시 1 리턴
     */
    public Long delete(Room room) {
        entityManager.remove(room);
        return 1L;
    }

}
