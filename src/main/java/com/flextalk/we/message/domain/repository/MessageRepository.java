package com.flextalk.we.message.domain.repository;

import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.room.domain.entity.Room;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.flextalk.we.message.domain.entity.QMessage.message;
import static com.flextalk.we.message.domain.entity.QMessageFile.messageFile;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public Message save(Message message) {
        entityManager.persist(message);
        return message;
    }

    public List<Message> findByRoom(Room room, int size, int offset) {
        return queryFactory.selectFrom(message)
                .leftJoin(message.messageFile, messageFile)
                .fetchJoin()
                .where(message.room.eq(room))
                .limit(size)
                .offset(offset)
                .orderBy(message.createDt.desc())
                .fetch();
    }

    public Optional<Message> findOne(Long messageId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(message)
                    .where(message.id.eq(messageId))
                    .fetchOne()
        );
    }
}
