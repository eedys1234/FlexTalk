package com.flextalk.we.message.domain.repository;

import com.flextalk.we.message.dto.MessageReadBulkInsertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageReadJdbcRepository {

    @Value("${batch_size}")
    protected int batchSize;

    private final String BULK_INSERT_SQL = "INSERT INTO ft_message_read (participant_id, message_id) VALUES(?, ?)";

    private final JdbcTemplate jdbcTemplate;

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

        jdbcTemplate.batchUpdate(BULK_INSERT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
//                        ZoneId zoneId = ZoneId.systemDefault();
                        ps.setLong(1, subList.get(i).getParticipantId());
                        ps.setLong(2, subList.get(i).getMessageId());
//                        ps.setTimestamp(3, new Timestamp(LocalDateTime.now().atZone(zoneId).toEpochSecond()));
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
}
