package com.lizumin.wms.service;

import com.lizumin.wms.MockRedisOperator;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserCacheServiceTest {
    private MockRedisOperator redisOperator = new MockRedisOperator();
    private UserCacheService userCacheService = new UserCacheService(redisOperator);

    /**
     * username不合法或不存在时put测试
     *
     */
    @Test
    public void should_return_null_when_username_not_in_cache_or_convert_error() {
        UserDetails result = this.userCacheService.getUserFromCache("");
        assertThat(result, nullValue());

        result = this.userCacheService.getUserFromCache(null);
        assertThat(result, nullValue());

        result = this.userCacheService.getUserFromCache("not_exist");
        assertThat(result, nullValue());

        redisOperator.set("test001", "not_a_user"); //放入错误的user json
        result = this.userCacheService.getUserFromCache("not_exist");
        assertThat(result, nullValue());

        redisOperator.remove("test001");
    }

    /**
     * putUserInCache getUserFromCache正常情况测试
     *
     */
    @Test
    public void should_insert_and_return_user_when_user_in_cache() {
        Set<GrantedAuthority> authorities = new HashSet<>(1);
        UserDetails user = new User.Builder().username("test_cache").password("test_cache")
                .authorities(SystemAuthority.defaults()).build();
        this.userCacheService.putUserInCache(user);

        UserDetails result = this.userCacheService.getUserFromCache("test_cache");
        assertThat(result, equalTo(user));
        redisOperator.remove("test_cache");
    }

    /**
     * 输入非User类时抛出错误测试
     *
     */
    @Test
    public void should_throw_assert_exception_when_put_non_User_class_obj() {
        UserDetails user = Mockito.mock(UserDetails.class); //不是User类
        Assertions.assertThrows(IllegalArgumentException.class ,() -> this.userCacheService.putUserInCache(user));
    }

    /**
     * 移除user测试
     *
     */
    @Test
    public void should_remove_user_when_user_in_cache() {
        userCacheService.removeUserFromCache("not_exist"); //不应该报错
        UserDetails user = new User.Builder().username("test_cache").password("test_cache")
                .authorities(SystemAuthority.defaults()).build();
        this.userCacheService.putUserInCache(user);

        assertThat(this.userCacheService.getUserFromCache("test_cache"), notNullValue());
        this.userCacheService.removeUserFromCache("test_cache");
        assertThat(this.userCacheService.getUserFromCache("test_cache"), nullValue());
    }
}
