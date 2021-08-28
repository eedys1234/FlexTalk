package com.flextalk.we.room.domain.repository;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomMessageDate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.flextalk.we.room.domain.entity.QRoomMessageDate.roomMessageDate;

@Repository
@RequiredArgsConstructor
public class RoomMessageDateRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public RoomMessageDate save(RoomMessageDate roomMessageDate) {
        entityManager.persist(roomMessageDate);
        return roomMessageDate;
    }

    public Optional<RoomMessageDate> findByRoomId(Room room) {
        return Optional.ofNullable(
                queryFactory.select(roomMessageDate)
                .from(roomMessageDate)
                .where(roomMessageDate.room.eq(room))
                .fetchOne()
        );
    }
}
