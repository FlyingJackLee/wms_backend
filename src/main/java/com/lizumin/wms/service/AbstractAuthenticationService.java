package com.lizumin.wms.service;

import com.lizumin.wms.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * 请求分解ID用service
 *
 * @author Zumin Li
 * @date 2024/3/1 15:41
 */
public abstract class AbstractAuthenticationService {
    protected int getOwnerId(Authentication authentication) {
        Assert.notNull(authentication, "authentication cannot be null");
        Object user = authentication.getPrincipal();
        Assert.isInstanceOf(User.class, user, "Principal must be a user");

        return ((User) user).getId();
    }
}
