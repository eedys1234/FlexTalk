package com.flextalk.we.user.domain.repository;

import com.flextalk.we.user.domain.entity.QUser;
import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.flextalk.we.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    public Optional<User> findOne(Long userId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne()
        );
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                .where(user.email.eq(email))
                .fetchOne()
        );
    }

    public List<User> findByIds(List<Long> userIds) {
        return queryFactory.selectFrom(user)
                .where(user.id.in(userIds))
                .fetch();

    }

    public List<User> findAll() {
        return queryFactory.selectFrom(user)
                .fetch();
    }

}
