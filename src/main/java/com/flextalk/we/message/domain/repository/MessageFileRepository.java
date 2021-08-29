package com.flextalk.we.message.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class MessageFileRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

}
