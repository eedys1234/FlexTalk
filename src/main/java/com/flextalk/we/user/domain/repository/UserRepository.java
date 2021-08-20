package com.flextalk.we.user.domain.repository;

import com.flextalk.we.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public User save(User user) {
        entityManager.persist(user);
        return user;
    }
}
