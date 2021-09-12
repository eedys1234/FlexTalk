package com.flextalk.we.cmmn.auth;

import com.flextalk.we.user.domain.entity.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPermission {
    Role  target() default Role.ROLE_NORMAL;
}
