package com.flextalk.we.participant.domain.repository;

import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.participant.domain.entity.QParticipant;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.flextalk.we.participant.domain.entity.QParticipant.*;

@Repository
@RequiredArgsConstructor
public class ParticipantRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public Participant save(Participant participant) {
        entityManager.persist(participant);
        return participant;
    }

    public List<Participant> findByUserId(User user) {
        return queryFactory.select(participant)
                .from(participant)
                .where(participant.user.eq(user))
                .fetch();
    }
}
