package com.flextalk.we.message.domain.repository;

import com.flextalk.we.cmmn.querydsl.OrderByNull;
import com.flextalk.we.message.domain.entity.MessageRead;
import com.flextalk.we.message.dto.MessageReadBulkInsertDto;
import com.flextalk.we.message.dto.MessageReadResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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

    public List<MessageReadResponseDto> findByMessages(List<Long> messageIds) {
        return queryFactory.select(Projections.constructor(MessageReadResponseDto.class, messageRead.message.id, messageRead.count()))
                .from(messageRead)
                .where(messageRead.message.id.in(messageIds))
                .groupBy(messageRead.message.id)
                .orderBy(OrderByNull.DEFAULT)
                .fetch();
    }

    public List<MessageRead> findAll() {
        return queryFactory.selectFrom(messageRead)
                .fetch();
    }
}
