package com.lizumin.wms.service;

import com.lizumin.wms.dao.AuthorityMapper;
import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Zumin Li
 * @date 2024/3/16 14:59
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AuthorityServiceTest {
    @MockBean
    private AuthorityMapper authorityMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserCacheService userCacheService;

    @Autowired
    private AuthorityService authorityService;

    /**
     * getRole getPermissions测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_call_mapper_when_getting() {
        when(this.authorityMapper.getRole(1)).thenReturn(new SystemAuthority(SystemAuthority.Role.DEFAULT.value()));

        SystemAuthority result = this.authorityService.getRole();
        assertThat(result.getAuthority(), equalTo(SystemAuthority.Role.DEFAULT.value()));
        verify(this.authorityMapper).getRole(1);

        this.authorityService.getPermission();
        verify(this.authorityMapper).getPermissions(1);
    }

    /**
     *  getPermissionByUserId权限错误测试 - default role
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_getting_permission_by_user() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
           this.authorityService.getPermissionsByUserId(3);
        });
    }

    /**
     *  getPermissionByUserId测试 - ROLE_OWNER
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER",groupId = 3)
    public void should_get_permission_or_throw_when_getting_permission() {
        // group 不一致
        when(this.userService.getUserByID(5)).thenReturn(
                new User.Builder().group(new Group.Builder().id(5).build()).build()
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.authorityService.getPermissionsByUserId(5);
        });

        // 正常
        when(this.userService.getUserByID(5)).thenReturn(
                new User.Builder().group(new Group.Builder().id(3).build()).build()
        );
        this.authorityService.getPermissionsByUserId(5);
        verify(this.authorityMapper).getPermissions(5);
    }

    /**
     *  updatePermission权限错误测试 - default role
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_update_permission() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.authorityService.updatePermission(1, true, false, false);
        });
    }

    /**
     *  updatePermission测试 - Owner role
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER",groupId = 3)
    public void should_throw_or_update_when_update_permission() {
        int userId = 5;

        // 1. user不存在
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.authorityService.updatePermission(userId, true, false, false);
        });

        // 2. group不一致
        User user = new User.Builder().id(userId).group(new Group.Builder().id(2).build()).build();
        when(this.userService.getUserByID(userId)).thenReturn(user);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.authorityService.updatePermission(userId, true, false, false);
        });

        // 3. 正常测试
        user = new User.Builder().id(userId).username("update_test").group(new Group.Builder().id(3).build()).build();
        when(this.userService.getUserByID(userId)).thenReturn(user);
        this.authorityService.updatePermission(userId, true, false, false);

        verify(this.authorityMapper).insertAuthority(userId, SystemAuthority.Permission.SHOPPING.value());
        verify(this.authorityMapper).deletePermission(userId, SystemAuthority.Permission.INVENTORY.value());
        verify(this.authorityMapper).insertAuthority(userId, SystemAuthority.Permission.SHOPPING.value());
        verify(this.userCacheService).removeUserFromCache("update_test");
    }
}

