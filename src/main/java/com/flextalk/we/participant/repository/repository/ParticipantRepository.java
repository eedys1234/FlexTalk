package com.flextalk.we.participant.repository.repository;

import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.flextalk.we.participant.repository.entity.QParticipant.*;
import static com.flextalk.we.room.domain.entity.QRoom.*;

@Repository
@RequiredArgsConstructor
public class ParticipantRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public Participant save(Participant participant) {
        entityManager.persist(participant);
        return participant;
    }

    public List<Participant> findByUser(User findUser) {
        return queryFactory.select(participant)
                .from(participant)
                .where(participant.user.eq(findUser))
                .fetch();
    }

    public List<Participant> findByRoom(Room findRoom) {
        return queryFactory.select(participant)
                .from(participant)
                .innerJoin(participant.room, room)
                .where(room.eq(findRoom))
                .fetch();
    }
}
