package com.flextalk.we.participant.repository.repository;

import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.QUser;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.flextalk.we.participant.repository.entity.QParticipant.*;
import static com.flextalk.we.room.domain.entity.QRoom.*;
import static com.flextalk.we.user.domain.entity.QUser.*;

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
                .innerJoin(participant.user, user)
                .fetchJoin()
                .where(room.eq(findRoom))
                .fetch();
    }

    public Optional<Participant> findOne(Long participantId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(participant)
                    .where(participant.id.eq(participantId))
                    .fetchOne()
        );
    }

    public Optional<Participant> findOwner(Long participantId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(participant)
                    .where(participant.id.eq(participantId), participant.isOwner.isTrue())
                    .fetchOne()
        );
    }

    public List<Participant> findByIds(List<Long> participantIds) {
        return queryFactory.selectFrom(participant)
                .where(participant.id.in(participantIds))
                .fetch();
    }
}
