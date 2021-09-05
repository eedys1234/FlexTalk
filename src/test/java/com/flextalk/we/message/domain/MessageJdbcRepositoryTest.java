//package com.flextalk.we.message.domain;
//
//import com.flextalk.we.message.domain.entity.MessageRead;
//import com.flextalk.we.message.domain.repository.MessageReadRepository;
//import com.flextalk.we.message.dto.MessageReadBulkInsertDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//import static java.util.stream.Collectors.toList;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//
//@Transactional
//@SpringBootTest
//@ActiveProfiles("test")
//public class MessageJdbcRepositoryTest {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private MessageReadRepository messageReadRepository;
//
//    private final String user_insert_sql = "insert into ft_user(user_email, user_password) values(?, ?)";
//
//    private final String room_insert_sql = "insert into ft_room(allow_participant_count, user_id, is_delete, room_name, " +
//            "room_limit_count, room_type) values(?, ?, ?, ?, ?, ?)";
//
//    private final String participant_insert_sql = "insert into ft_participant(is_alarm, is_bookmark, is_owner, room_id, " +
//            "user_id) values(?, ?, ?, ?, ?)";
//
//    private final String message_insert_sql = "insert into ft_message(is_delete, message_content, message_type, " +
//            "participant_id, room_id) values(?, ?, ?, ?, ?)";
//
//    private final String message_select_sql = "select * from ft_message";
//
//    @BeforeEach
//    public void init() {
//
//        jdbcTemplate.update(user_insert_sql, "test1@gmail.com", "123!@#DDDDD");
//        jdbcTemplate.update(user_insert_sql, "test2@gmail.com", "123!@#DDDDD");
//
//        String roomName = "테스트 채팅방";
//        String roomType = "NORMAL";
//        int roomLimitCount = 2;
//
//        jdbcTemplate.update(room_insert_sql, roomLimitCount, 1, false, roomName, roomLimitCount, roomType);
//
//        jdbcTemplate.update(participant_insert_sql, true, false, true, 1, 1);
//        jdbcTemplate.update(participant_insert_sql, true, false, false, 1, 2);
//
//        for(int i=0;i<10000;i++) {
//            jdbcTemplate.update(message_insert_sql, false, "테스트", "TEXT", 1, 1);
//        }
//
//    }
//
//    @DisplayName("메시지 읽기 테스트")
//    @Test
//    public void readMessageTest() {
//
//        List<Long> message_ids = jdbcTemplate.query(message_select_sql, new RowMapper<Long>() {
//            @Override
//            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
//                return rs.getLong("message_id");
//            }
//        });
//
//        List<MessageReadBulkInsertDto> messageReads = message_ids.stream()
//                .map(id -> new MessageReadBulkInsertDto(1L, id))
//                .collect(toList());
//
//        //when
//        messageReadRepository.saveAll(messageReads);
//
//        //then
//        List<MessageRead> allReads = messageReadRepository.findAll();
//        assertThat(allReads.size(), equalTo(message_ids.size()));
//    }
//
//}
