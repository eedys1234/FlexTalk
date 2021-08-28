package com.flextalk.we.message.domain.repository;

import com.flextalk.we.cmmn.querydsl.OrderByNull;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.entity.MessageRead;
import com.flextalk.we.message.dto.MessageReadDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.flextalk.we.message.domain.entity.QMessageRead.*;

@Repository
@RequiredArgsConstructor
public class MessageReadRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public MessageRead save(MessageRead messageRead) {
        entityManager.persist(messageRead);
        return messageRead;
    }

    public List<MessageReadDto> findByMessages(List<Long> messageIds) {
        return queryFactory.select(Projections.constructor(MessageReadDto.class, messageRead.message.id, messageRead.count()))
                .from(messageRead)
                .where(messageRead.message.id.in(messageIds))
                .groupBy(messageRead.message.id)
                .orderBy(OrderByNull.DEFAULT)
                .fetch();
    }
}
