package com.flextalk.we.cmmn.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.Modifying;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
//TODO : MappedSuperclass에 대해서 공부하기, + Auditing 기능
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @LastModifiedDate
    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
