package com.flextalk.we.room.domain.repository;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomBookMark;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.flextalk.we.room.domain.entity.QRoomBookMark.*;

@Repository
@RequiredArgsConstructor
public class RoomBookMarkRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public RoomBookMark save(RoomBookMark roomBookMark) {
        entityManager.persist(roomBookMark);
        return roomBookMark;
    }

    public Optional<RoomBookMark> findOne(Long id) {
        return Optional.ofNullable(
                entityManager.find(RoomBookMark.class, id)
        );
    }

    public List<RoomBookMark> findByUser(User user) {
        return queryFactory.select(roomBookMark)
                .from(roomBookMark)
                .where(roomBookMark.user.eq(user))
                .fetch();
    }

    public List<RoomBookMark> findByRooms(List<Room> rooms, User user) {
        return queryFactory.select(roomBookMark)
                .from(roomBookMark)
                .where(roomBookMark.room.in(rooms), roomBookMark.user.eq(user))
                .fetch();
    }
}
