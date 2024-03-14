package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Zumin Li
 * @date 2024/3/12 0:38
 */
@SpringBootTest
@ActiveProfiles(value = "test")
public class GroupMapperTest {
    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * getGroupByUserId测试
     */
    @Test
    public void should_get_relevant_group(){
        Group result = this.groupMapper.getGroupByUserId(1);
        assertThat(result.getId(), is(0));

        result = this.groupMapper.getGroupByUserId(2);
        assertThat(result.getId(), is(1));
    }

    /**
     * updateGroup测试
     */
    @Test
    public void should_update_group_or_throw() {
        this.groupMapper.updateGroup(2, 2);
        Group result = this.groupMapper.getGroupByUserId(2);
        assertThat(result.getId(), is(2));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.groupMapper.updateGroup(2, 999);
        });
    }

    /**
     * insertGroup测试
     */
    @Test
    public void should_get_valid_id_when_insert() {
        int id = this.groupMapper.insertGroup("测试", "测试地址", "", new Date());
        assertThat(id, greaterThan(0));
        // 重复插入不报错
        this.groupMapper.insertGroup("测试", "测试地址", "", new Date());
    }

    /**
     * insertRequest测试
     */
    @Test
    public void should_insert_request_or_throw_exception() {
        //  正常插入无异常
        User user = new User.Builder().username("inserttest1").password("test1234").build();
        this.userMapper.insertUser(user);
        int groupId = this.groupMapper.insertGroup("测试2", "测试地址2", "", new Date());

        this.groupMapper.insertRequest(user.getId(), groupId);

        // 参数有误
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.groupMapper.insertRequest(user.getId(), 99);
        });
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.groupMapper.insertRequest(99, groupId);
        });

        // 重复插入
        Assertions.assertThrows(DuplicateKeyException.class, () -> {
            this.groupMapper.insertRequest(user.getId(), groupId);
        });
    }

    /**
     * deleteRequest测试
     */
    @Test
    public void should_delete_request() {
        User user = new User.Builder().username("deletetest1").password("test1234").build();
        this.userMapper.insertUser(user);
        int groupId = this.groupMapper.insertGroup("测试3", "测试地址3", "", new Date());
        this.groupMapper.insertRequest(user.getId(), groupId);

        this.groupMapper.deleteRequest(user.getId());
        assertThat(this.groupMapper.getGroupByUserIdInRequest(user.getId()), nullValue());
    }

    /**
     * getGroupByUserIdInRequest测试
     */
    @Test
    public void should_get_group_when_retrieval() {
        User user = new User.Builder().username("gettestrequest1").password("test1234").build();
        this.userMapper.insertUser(user);
        int groupId = this.groupMapper.insertGroup("测试4", "测试地址4", "2", new Date());
        this.groupMapper.insertRequest(user.getId(), groupId);

        Group result = this.groupMapper.getGroupByUserIdInRequest(user.getId());
        assertThat(result.getId(), equalTo(groupId));

        result = this.groupMapper.getGroupByUserIdInRequest(99);
        assertThat(result, nullValue());
    }

    /**
     * getRequestUsersByGroupId测试
     */
    @Test
    public void should_get_relevant_users_with_group_id() {
        User user1 = new User.Builder().username("listtestrequest1").password("test1234").build();
        User user2 = new User.Builder().username("listtestrequest2").password("test1234").build();
        this.userMapper.insertUser(user1);
        this.userMapper.insertUser(user2);
        int groupId = this.groupMapper.insertGroup("测试5", "测试地址5", "2", new Date());
        this.groupMapper.insertRequest(user1.getId(), groupId);
        this.groupMapper.insertRequest(user2.getId(), groupId);

        List<User> users = this.groupMapper.getRequestUsersByGroupId(groupId);
        assertThat(users.size(), is(2));

        users = this.groupMapper.getRequestUsersByGroupId(99);
        assertThat(users, empty());
    }
}
