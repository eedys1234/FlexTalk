package com.flextalk.we.room.domain.repository;

import com.flextalk.we.room.domain.entity.QRoom;
import com.flextalk.we.room.domain.entity.QRoomAlarm;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.entity.RoomAlarm;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.flextalk.we.room.domain.entity.QRoomAlarm.roomAlarm;

@Repository
@RequiredArgsConstructor
public class RoomAlarmRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public List<RoomAlarm> findByUserId(User user) {
        return queryFactory.select(roomAlarm)
                .from(roomAlarm)
                .where(roomAlarm.user.eq(user))
                .fetch();
    }
}
