package com.lizumin.wms.dao;

import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Zumin Li
 * @date 2024/3/12 20:10
 */
@SpringBootTest
@ActiveProfiles(value = "test")
public class AuthorityMapperTest {
    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 测试user不存在或者输入不合法时插入authority
     *
     */
    @Test
    public void should_not_insert_authority_when_user_id_not_exist_or_authority_empty() {
        // user id not exist: throw DataIntegrityViolationException
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> this.authorityMapper.insertAuthority(-1, SystemAuthority.Role.ADMIN.value()));

        // authority empty: throw DataIntegrityViolationException
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> this.authorityMapper.insertAuthority(2, ""));
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> this.authorityMapper.insertAuthority(2, null));
    }

    /**
     * 正常插入authority
     *
     */
    @Test
    public void should_insert_authority_when_user_id_exist() {
        this.authorityMapper.insertAuthority(2, SystemAuthority.Permission.STATISTICS.value());

        User user = this.userMapper.getUserByUsername("testmodified");
        assertThat(user.getAuthorities().stream().anyMatch(item -> item.getAuthority().equals(SystemAuthority.Permission.STATISTICS.value())), is(true));
    }

    /**
     * getRole测试
     */
    @Test
    public void should_get_relating_role() {
        SystemAuthority authority = this.authorityMapper.getRole(1);
        assertThat(authority.getAuthority(), equalTo(SystemAuthority.Role.DEFAULT.value()));

        authority = this.authorityMapper.getRole(99);
        assertThat(authority, nullValue());
    }

    /**
     * updateRole测试
     */
    @Test
    public void should_update_role_when_user_exist() {
        this.authorityMapper.updateRole(2, SystemAuthority.Role.ADMIN.value());
        assertThat(this.authorityMapper.getRole(1).getAuthority(), equalTo(SystemAuthority.Role.DEFAULT.value()));
    }

    /**
     * getPermissions测试
     */
    @Test
    public void should_get_permissions() {
        List<SystemAuthority> authorities = this.authorityMapper.getPermissions(1);
        assertThat(authorities, empty());

        authorities = this.authorityMapper.getPermissions(2);
        assertThat(authorities.size(), is(2));
        assertThat(authorities.getFirst().getAuthority(), equalTo(SystemAuthority.Permission.SHOPPING.value()));
        assertThat(authorities.getLast().getAuthority(), equalTo(SystemAuthority.Permission.INVENTORY.value()));
    }

    /**
     * deletePermission测试
     */
    @Test
    public void should_only_delete_with_permission() {
        int originalAmount = this.authorityMapper.getAuthorities(2).size();

        this.authorityMapper.deletePermission(2, SystemAuthority.Permission.SHOPPING.value());
        assertThat(originalAmount -1, is(this.authorityMapper.getAuthorities(2).size()));

        this.authorityMapper.deletePermission(2, SystemAuthority.Role.STAFF.value());
        assertThat(originalAmount - 1, is(this.authorityMapper.getAuthorities(2).size()));
    }


    /**
     * getAuthorities
     */
    @Test
    public void should_get_authorities() {
        List<SystemAuthority> authorities = this.authorityMapper.getAuthorities(1);
        assertThat(authorities.size(), is(this.authorityMapper.getPermissions(1).size() + 1));
    }
}
