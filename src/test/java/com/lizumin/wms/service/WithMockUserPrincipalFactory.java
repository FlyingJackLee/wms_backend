package com.lizumin.wms.service;

import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于测试时快速模拟指定登录用户信息
 *
 * @author Zumin Li
 * @date 2024/3/14 16:59
 */
public class WithMockUserPrincipalFactory implements WithSecurityContextFactory<WithMockUserPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserPrincipal annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Group group = new Group.Builder().id(annotation.groupId()).build();
        User user = new User.Builder().username(annotation.username()).password("123456").id(annotation.id()).group(group).build();
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(user, user.getUsername(),
                stringsToAuthorities(annotation.role(), annotation.permissions()));
        context.setAuthentication(auth);
        return context;
    }

    private List<SystemAuthority> stringsToAuthorities(String role , String[] permissions) {
        List<SystemAuthority> result = new ArrayList<>(0);
        result.add(new SystemAuthority(role));
        for (String permission:permissions) {
            result.add(new SystemAuthority(permission));
        }
        return result;
    }
}
