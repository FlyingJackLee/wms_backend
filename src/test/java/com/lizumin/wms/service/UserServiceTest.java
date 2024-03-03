package com.lizumin.wms.service;

import com.lizumin.wms.dao.UserMapper;
import com.lizumin.wms.entity.SimpleAuthority;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.exception.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserMapper userMapper;

    private UserCache userCache;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    public UserServiceTest() {
        userMapper = Mockito.mock(UserMapper.class);
        when(userMapper.getUserByUsername(null)).thenReturn(null);
        when(userMapper.getUserByUsername("")).thenReturn(null);
        when(userMapper.getUserByUsername("not_exist")).thenReturn(null);

        userCache = Mockito.mock(UserCache.class);
        when(userCache.getUserFromCache(null)).thenReturn(null);
        when(userCache.getUserFromCache("")).thenReturn(null);
        when(userCache.getUserFromCache("not_exist")).thenReturn(null);

        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        userService = new UserService(userMapper, userCache, passwordEncoder);
    }

    /**
     * username不合法或不存在时loadUserByUsername测试
     *
     */
    @Test
    public void should_throw_authentication_exception_when_username_is_blank_or_not_in_db() {
        Assertions.assertThrows(AuthenticationException.class, () -> userService.loadUserByUsername(null));
        Assertions.assertThrows(AuthenticationException.class, () -> userService.loadUserByUsername(""));
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.loadUserByUsername("not_exist"));
    }

    /**
     * username存在时loadUserByUsername测试
     *
     */
    @Test
    public void should_get_user_when_username_exist() {
        final String username = "test";
        when(userMapper.getUserByUsername(username)).thenReturn(new User());
        assertThat(userService.loadUserByUsername(username), notNullValue());
    }

    /**
     * username不合法或不存在时lazyLoadUserByUsername测试
     *
     */
    @Test
    public void should_throw_authentication_exception_when_username_is_blank_or_not_in_db_and_cache() {
        Assertions.assertThrows(AuthenticationException.class, () -> userService.lazyLoadUserByUsername(null));
        Assertions.assertThrows(AuthenticationException.class, () -> userService.lazyLoadUserByUsername(""));
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.lazyLoadUserByUsername("not_exist"));
    }

    /**
     * username存在db或者cache时lazyLoadUserByUsername测试
     *
     */
    @Test
    public void should_get_user_when_username_in_db_or_cache() {
        when(userCache.getUserFromCache("test")).thenReturn(new User());
        assertThat(userService.lazyLoadUserByUsername("test"), notNullValue());

        when(userCache.getUserFromCache("test")).thenReturn(null);
        when(userMapper.getUserByUsername("test")).thenReturn(new User());
        assertThat(userService.lazyLoadUserByUsername("test"), notNullValue());
    }

    /**
     * getUsernameByEmail/getUsernameByPhoneNumber测试
     *
     */
    @Test
    public void should_get_relevant_result_when_run_get_username() {
        when(userMapper.getUsernameByEmail("test@test.com")).thenReturn("test001");
        when(userMapper.getUsernameByPhoneNumber("13012341234")).thenReturn("test001");

        assertThat(userService.getUsernameByEmail(null), nullValue());
        assertThat(userService.getUsernameByEmail(""), nullValue());
        assertThat(userService.getUsernameByEmail("not_exist"), nullValue());
        assertThat(userService.getUsernameByEmail("test@test.com"), equalTo("test001"));

        assertThat(userService.getUsernameByPhoneNumber(null), nullValue());
        assertThat(userService.getUsernameByPhoneNumber(""), nullValue());
        assertThat(userService.getUsernameByPhoneNumber("not_exist"), nullValue());
        assertThat(userService.getUsernameByPhoneNumber("13012341234"), equalTo("test001"));
    }

    /**
     * username/email/phone不存在db时, 测试isUsernameExist/isPhoneExist/isEmailExist
     *
     */
    @Test
    public void should_return_false_when_parameter_not_in_db() {
        assertThat(userService.isUsernameExist(null) ,is(false));
        assertThat(userService.isUsernameExist("") ,is(false));
        assertThat(userService.isUsernameExist("not_exist") ,is(false));

        assertThat(userService.isPhoneExist(null) ,is(false));
        assertThat(userService.isPhoneExist("") ,is(false));
        assertThat(userService.isPhoneExist("not_exist") ,is(false));

        assertThat(userService.isEmailExist(null) ,is(false));
        assertThat(userService.isEmailExist("") ,is(false));
        assertThat(userService.isEmailExist("not_exist") ,is(false));
    }

    /**
     * username/email/phone存在db时, 测试isUsernameExist/isPhoneExist/isEmailExist
     *
     */
    @Test
    public void should_return_true_when_parameter_in_db() {
        when(userMapper.isUsernameExist("test001")).thenReturn(true);
        when(userMapper.isEmailExist("test@test.com")).thenReturn(true);
        when(userMapper.isPhoneExist("13612341234")).thenReturn(true);

        assertThat(userService.isUsernameExist("test001") ,is(true));
        assertThat(userService.isEmailExist("test@test.com") ,is(true));
        assertThat(userService.isPhoneExist("13612341234") ,is(true));
    }

    /**
     * 正常插入user测试
     *
     */
    @Test
    public void should_return_id_when_insert_a_valid_user() {
        // 1. 不插入email, phone
        Set<GrantedAuthority> authorities = new HashSet<>(2);
        authorities.add(new SimpleAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleAuthority("WRITE_ONLY"));
        User user = new User.Builder().username("test001").password("test001").authorities(authorities).build();

        doAnswer(answer -> {
            User temp = (User) answer.getArgument(0);
            temp.setId(5);
            return null;
        }).when(userMapper).insertUser(user);

        int result = userService.insertUser(user);
        assertThat(result, is(5));
        verify(userMapper, times(1)).insertUser(any());
        verify(userMapper, times(2)).insertAuthority(anyInt() ,any());

        // 2. 只插入email
        result = userService.insertUser(user, "a@a.com", null);
        verify(userMapper).updateEmail(5, "a@a.com");
        assertThat(result, is(5));

        // 3. 只插入phone
        result = userService.insertUser(user, null, "13012341234");
        verify(userMapper).updatePhone(5, "13012341234");
        assertThat(result, is(5));

        // 4. 插入email phone
        result = userService.insertUser(user, "a2@a.com", "13112341234");
        verify(userMapper).updateEmail(5, "a2@a.com");
        verify(userMapper).updatePhone(5, "13112341234");
        assertThat(result, is(5));
    }

    /**
     * 通过邮箱重置密码测试
     */
    @Test
    public void should_set_encrypted_password_when_reset_by_email() {
        when(passwordEncoder.encode("test000")).thenReturn("encryted");
        userService.resetPassword("test@test.com", "test000");
        verify(passwordEncoder).encode("test000");
        verify(userMapper).updatePasswordByEmail("test@test.com", "encryted");
    }
}
