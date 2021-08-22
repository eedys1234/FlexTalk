package com.flextalk.we.room.repository;

import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.flextalk.we.participant.repository.entity.QParticipant.*;
import static com.flextalk.we.room.domain.entity.QRoom.room;
import static com.flextalk.we.room.domain.entity.QRoomMessageDate.roomMessageDate;

@Repository
@RequiredArgsConstructor
public class RoomRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public Room save(Room room) {
        entityManager.persist(room);
        return room;
    }

    /**
     * 사용자가 참여하고 있는 채팅방정보 가져오기
     * 기준이 통해 정렬 후 가져오는게 필요함(BookMark, 메세지 온순) -> 4중 조인 가능?
     * participatn의 room id에 인덱스를 걸어야함(mysql에서는 외래키이기 때문에 자동 인덱스 생성됨)
     * 속도 저하되면 sql보다는 비즈니스 로직으로 풀어가야할 듯(테스트 필요)
     * @param user 사용자
     * @return 사용자의 채팅방 리스트
     */
    public List<Room> findByUserId(User user) {
        return queryFactory.select(room)
                .from(room)
                .innerJoin(room.participants, participant)
                .leftJoin(room.roomMessageDate, roomMessageDate)
                .fetchJoin()
                .where(participant.user.eq(user))
                .orderBy(roomMessageDate.roomMessageRecentDate.desc())
                .fetch();
    }

    public Optional<Room> findOne(Long id) {
        return Optional.ofNullable(
            entityManager.find(Room.class, id)
        );
    }

    /**
     * 채팅방 삭제
     * @param room 채팅방
     * @return 성공시 1 리턴
     */
    public Long delete(Room room) {
        entityManager.remove(room);
        return 1L;
    }
}
