package com.lizumin.wms.service;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 模拟登录用户
 *
 * @author Zumin Li
 * @date 2024/3/14 16:59
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserPrincipalFactory.class)
public @interface WithMockUserPrincipal {
    int id() default 1;

    String username() default "test";

    String password() default "12345678";

    String role() default "ROLE_DEFAULT";

    String[] permissions() default {};

    int groupId() default 0;
}
