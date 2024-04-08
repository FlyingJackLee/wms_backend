package com.lizumin.wms.dao;

import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles(value = "test")
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户不存在时查找User测试
     *
     */
    @Test
    public void should_get_null_when_user_not_exist_in_db() {
        User result = this.userMapper.getUserByUsername("not_exist");
        assertThat(result, nullValue());

        result = this.userMapper.getUserByUsername("");
        assertThat(result, nullValue());

        result = this.userMapper.getUserByUsername(null);
        assertThat(result, nullValue());
    }

    /**
     * 查找User测试
     *
     */
    @Test
    public void should_get_user_when_username_exist_in_db() {
        User result = this.userMapper.getUserByUsername("test001");
        assertThat(result.getUsername(), equalTo("test001"));
        assertThat(result.getGroup().getId(), is(0)); // 默认为0 缺省组
    }

    /**
     * getUserById测试
     *
     */
    @Test
    public void should_get_user_with_id() {
        User result = this.userMapper.getUserById(1);
        assertThat(result.getUsername(), equalTo("test001"));

        result = this.userMapper.getUserById(99);
        assertThat(result, nullValue());
    }

    /**
     * 邮箱不存在或者违反格式规则时getUsernameByEmail查询测试
     *
     */
    @Test
    public void should_get_null_when_email_not_exist_in_db_or_violate_check() {
        String result = this.userMapper.getUsernameByEmail("not_exist@a.com");
        assertThat(result, nullValue());

        result = this.userMapper.getUsernameByEmail("");
        assertThat(result, nullValue());

        result = this.userMapper.getUsernameByEmail(null);
        assertThat(result, nullValue());

        result = this.userMapper.getUsernameByEmail("not_a_email");
        assertThat(result, nullValue());
    }

    /**
     * 号码不存在或者违反格式规则时getUsernameByPhoneNumber查询测试
     *
     */
    @Test
    public void should_get_null_when_phone_number_not_exist_in_db_or_violate_check() {
        String result = this.userMapper.getUsernameByPhoneNumber("13000000000"); // 13000000000 not in db
        assertThat(result, nullValue());

        result = this.userMapper.getUsernameByPhoneNumber("");
        assertThat(result, nullValue());

        result = this.userMapper.getUsernameByPhoneNumber(null);
        assertThat(result, nullValue());


        result = this.userMapper.getUsernameByPhoneNumber("1234");
        assertThat(result, nullValue());
    }

    /**
     * getUsernameByPhoneNumber getUsernameByEmail查找Username测试
     *
     */
    @Test
    public void should_get_username_when_email_or_phone_exist_in_db() {
        String result = this.userMapper.getUsernameByPhoneNumber("13012341234");
        assertThat(result, equalTo("test001"));

        result = this.userMapper.getUsernameByEmail("test@test.com");
        assertThat(result, equalTo("test001"));
    }

    /**
     * 不存在时检查字段测试
     *
     */
    @Test
    public void should_get_true_when_email_username_phone_exist_in_db() {
        boolean result = this.userMapper.isUsernameExist("test001");
        assertThat(result, is(true));

        result = this.userMapper.isEmailExist("test@test.com");
        assertThat(result, is(true));

        result = this.userMapper.isPhoneExist("13012341234");
        assertThat(result, is(true));
    }

    /**
     * 存在时检查字段测试
     *
     */
    @Test
    public void should_get_false_when_email_username_phone_not_exist_in_db() {
        boolean result = this.userMapper.isUsernameExist("not_exist");
        assertThat(result, is(false));

        result = this.userMapper.isEmailExist("not_exist");
        assertThat(result, is(false));

        result = this.userMapper.isPhoneExist("not_exist");
        assertThat(result, is(false));
    }
    
    /**
     * User正常为新时插入User测试
     * 
     */
    @Test
    public void should_insert_user_when_user_is_valid() {
        User user = new User.Builder().username("test002").password("test1234").build();
        this.userMapper.insertUser(user);
        assertThat(this.userMapper.getUserByUsername("test002"), equalTo(user));
    }

    /**
     * 输入信息有误时插入User
     *
     */
    @Test
    public void should_not_insert_user_when_user_is_exist_or_miss_parameter() {
        // user exist : throw DuplicateKeyException
        final User user = new User.Builder().username("test001").password("test1234").build();
        Assertions.assertThrows(DuplicateKeyException.class, () -> this.userMapper.insertUser(user));

        // miss username : throw DataIntegrityViolationException
        final User userMissUsername = new User.Builder().username("").password("test1234").build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> this.userMapper.insertUser(userMissUsername));

        // missing password： throw DataIntegrityViolationException
        final User userMissingPassword = new User.Builder().username("test003").password("").build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> this.userMapper.insertUser(userMissingPassword));
    }

    /**
     * 正常插入email/phone
     *
     */
    @Test
    public void should_insert_detail_when_detail_valid() {
        User user = new User.Builder().username("detailtest1").password("test1234").build();
        this.userMapper.insertUser(user);

        // 1 插入phone
        this.userMapper.updatePhone(user.getId(), "13111111111");
        assertThat(this.userMapper.getUsernameByPhoneNumber("13111111111"), notNullValue());

        // 2 插入email
        this.userMapper.updateEmail(user.getId(), "testdetail@test.com");
        assertThat(this.userMapper.getUsernameByEmail("testdetail@test.com"), notNullValue());
    }

    /**
     * getGroupIdByPhone测试
     */
    @Test
    public void should_get_group_id_when_query() {
        Integer result = this.userMapper.getGroupIdByPhone("13212341234");
        assertThat(result, equalTo(1));

        result = this.userMapper.getGroupIdByPhone("9999999996");
        assertThat(result, nullValue());
    }

    /**
     * 正常插入email/phone
     *
     */
    @Test
    public void should_throw_exception_when_detail_invalid() {
        // 1. 字段错误
        User user = new User.Builder().username("detailtest2").password("test1234").build();
        this.userMapper.insertUser(user);
        Assertions.assertThrows(DataIntegrityViolationException.class,() -> {this.userMapper.updatePhone(user.getId(), "13100000");});
        Assertions.assertThrows(DataIntegrityViolationException.class,() -> {this.userMapper.updateEmail(user.getId(), "aas2213");});

        // 2. 已经存在
        Assertions.assertThrows(DuplicateKeyException.class,() -> {this.userMapper.updatePhone(user.getId(), "13012341234");});
        Assertions.assertThrows(DuplicateKeyException.class,() -> {this.userMapper.updateEmail(user.getId(), "test@test.com");});
    }

    /**
     * 修改密码测试
     *
     */
    @Test
    public void should_update_password_when_giving_a_valid_email_and_password() {
        User user = new User.Builder().username("passwordresettest").password("test1234").build();
        this.userMapper.insertUser(user);
        this.userMapper.updateEmail(user.getId(), "passwordresettest@test.com");
        this.userMapper.updatePasswordByEmail("passwordresettest@test.com", "resettest123");

        User modifedUser = this.userMapper.getUserByUsername(user.getUsername());
        assertThat(modifedUser.getPassword(), equalTo("resettest123"));
    }

    /**
     * getProfile测试
     */
    public void should_get_null_or_detail() {
        User user = new User.Builder().username("getprofiletest").password("test1234").build();
        this.userMapper.insertUser(user);
        assertThat(this.userMapper.getProfile(user.getId()), nullValue());

        this.userMapper.updateEmail(user.getId(), "getprofiletest@test.com");
        assertThat(this.userMapper.getProfile(user.getId()).getEmail(), equalTo("getprofiletest@test.com"));
    }

    /**
     * updateNickname测试
     *
     */
    @Test
    public void should_throw_exception_or_update_nickname() {
        User user = new User.Builder().username("nicknametest").password("test1234").build();
        this.userMapper.insertUser(user);

        this.userMapper.updateNickname(user.getId(), "nicktest");

        assertThat(this.userMapper.getProfile(user.getId()).getNickname(), equalTo("nicktest"));
    }
}