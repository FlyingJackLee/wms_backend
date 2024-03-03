package com.lizumin.wms.security;

import com.lizumin.wms.entity.User;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * @author Zumin Li
 * @date 2024/2/14 19:41
 */
public class UserAuthenticationProvider extends DaoAuthenticationProvider {
    /**
     * 确保放进principal的对象包括自定义User的信息
     *
     * @param principal that should be the principal in the returned object (defined by
     * the {@link #isForcePrincipalAsString()} method)
     * @param authentication that was presented to the provider for validation
     * @param user that was loaded by the implementation
     * @return
     */
    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        Assert.isInstanceOf(User.class, user, () -> "Authenticated UserDetails must be a User");
        principal = user;
        return super.createSuccessAuthentication(principal, authentication, user);
    }
}
