package com.flextalk.we.message.domain;

import com.flextalk.we.message.domain.repository.MessageReadJdbcRepository;
import org.springframework.jdbc.core.JdbcTemplate;

public class MockMessageReadJdbcRepository extends MessageReadJdbcRepository {

    public MockMessageReadJdbcRepository(JdbcTemplate jdbcTemplate, int batchSize) {
        super(jdbcTemplate);
        super.batchSize = batchSize;
    }

}
