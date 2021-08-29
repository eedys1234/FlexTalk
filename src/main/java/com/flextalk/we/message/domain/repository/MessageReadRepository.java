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

import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.flextalk.we.message.domain.entity.QMessageRead.*;

@Repository
@RequiredArgsConstructor
public class MessageReadRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Value("${batch_size}")
    private int batchSize;

    public MessageRead save(MessageRead messageRead) {
        entityManager.persist(messageRead);
        return messageRead;
    }

    public void saveAll(List<MessageReadBulkInsertDto> list) {

        int batchCount = 0;
        List<MessageReadBulkInsertDto> subList = new ArrayList<>();

        for(int i=0;i<list.size();i++) {
            subList.add(list.get(i));
            if((i+1) % batchSize == 0) {
                batchCount = batchInsert(batchCount, subList);
            }
        }

        if(!subList.isEmpty()) {
            batchInsert(batchCount, subList);
        }
    }

    private int batchInsert(int batchCount, List<MessageReadBulkInsertDto> subList) {

        jdbcTemplate.batchUpdate("INSERT INTO ft_message_read (`participant_id`,`message_id`) VALUES(?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, subList.get(i).getParticipantId());
                        ps.setLong(2, subList.get(i).getMessageId());
                    }

                    @Override
                    public int getBatchSize() {
                        return subList.size();
                    }
                });

        subList.clear();
        batchCount++;
        return batchCount;
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
