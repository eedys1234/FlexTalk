package com.flextalk.we.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_ADMIN("ROLE_ADMIN", "관리자", 1, null),
    ROLE_NORMAL("ROLE_NORMAL", "일반사용자", 2, ROLE_ADMIN);

    private final String key;
    private final String value;
    private final int priority;
    private final Role nextRole;
}