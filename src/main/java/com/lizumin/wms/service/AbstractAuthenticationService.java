package com.lizumin.wms.service;

import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * 请求分解ID用service
 *
 * @author Zumin Li
 * @date 2024/3/1 15:41
 */
public abstract class AbstractAuthenticationService {
    protected int getUserId(Authentication authentication) {
        Assert.notNull(authentication, "authentication cannot be null");
        Object user = authentication.getPrincipal();
        Assert.isInstanceOf(User.class, user, "Principal must be a user");

        return ((User) user).getId();
    }

    /**
     * 获取当前用户id
     *
     */
    protected int getUserId() {
        return getUser().getId();
    }

    /**
     * 获取当前用户
     *
     */
    protected User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.notNull(authentication, "authentication cannot be null");
        Object user = authentication.getPrincipal();
        Assert.isInstanceOf(User.class, user, "Principal must be a user");

        return (User) user;
    }

    /**
     * 获取当前用户group
     *
     */
    protected int getGroupId() {
        int groupId = this.getUser().getGroup().getId();
        if (groupId <= 0) {
            throw  new AccessDeniedException("Do not join any group");
        }

        return this.getUser().getGroup().getId();
    }
}
