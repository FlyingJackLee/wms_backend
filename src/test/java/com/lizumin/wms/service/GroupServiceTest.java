package com.lizumin.wms.service;

import com.lizumin.wms.dao.AuthorityMapper;
import com.lizumin.wms.dao.GroupMapper;
import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.entity.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/14 0:11
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class GroupServiceTest {
    @MockBean
    private GroupMapper groupMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthorityMapper authorityMapper;

    @MockBean
    private UserCacheService userCacheService;

    @Autowired
    private GroupService groupService;

    /**
     * getGroupOfCurrentUser测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_call_mapper_when_get_group() {
        this.groupService.getGroupOfCurrentUser();
        Mockito.verify(this.groupMapper).getGroupByUserId(1);
    }

    /**
     * createGroup测试 - default身份(合法)
     */
    @Test
    @WithMockUserPrincipal
    public void should_throw_or_create_with_default_role() {
        Date dateStub = new Date();

        // 1.参数错误测试
        Mockito.when(this.groupMapper.insertGroup("getTest", "", "", dateStub)).thenReturn(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.createGroup("", "", "", dateStub);
        });

        Mockito.when(this.groupMapper.insertGroup("getTest", "", "", dateStub)).thenReturn(0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.createGroup("getTest", "", "", dateStub);
        });

        // 2.正常创建测试
        String storeNameStub = "insert_test_store";
        String addressStub = "insert_test_address";
        Mockito.when(this.groupMapper.insertGroup(storeNameStub, addressStub, "", dateStub)).thenReturn(3);
        this.groupService.createGroup(storeNameStub, addressStub, null, dateStub);
        Mockito.verify(this.groupMapper).insertGroup(storeNameStub, addressStub, "", dateStub);
        verify(this.authorityMapper).updateRole(1,SystemAuthority.Role.OWNER.value());
        Mockito.verify(this.groupMapper).updateGroupOfUser(1,3);
    }

    /**
     * createGroup测试 - Owner身份(不合法)
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER")
    public void should_throw_access_denied_exception_with_owner_role() {
        Date dateStub = new Date();
        String storeNameStub = "insert_test_store";
        String addressStub = "insert_test_address";
        Mockito.when(this.groupMapper.insertGroup(storeNameStub, addressStub, "", dateStub)).thenReturn(3);
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.createGroup(storeNameStub, addressStub, null, dateStub);
        });
    }

    /**
     * createGroup测试 - Staff身份(不合法)
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF")
    public void should_throw_access_denied_exception_with_staff_role() {
        Date dateStub = new Date();
        String storeNameStub = "insert_test_store";
        String addressStub = "insert_test_address";
        Mockito.when(this.groupMapper.insertGroup(storeNameStub, addressStub, "", dateStub)).thenReturn(3);
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.createGroup(storeNameStub, addressStub, null, dateStub);
        });
    }

    /**
     * createGroup测试 - ADMIN管理员身份(合法)
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_ADMIN")
    public void should_create_group_and_update_user() {
        Date dateStub = new Date();
        String storeNameStub = "insert_test_store";
        String addressStub = "insert_test_address";
        Mockito.when(this.groupMapper.insertGroup(storeNameStub, addressStub, "", dateStub)).thenReturn(3);

        this.groupService.createGroup(storeNameStub, addressStub, null, dateStub);
        Mockito.verify(this.groupMapper).insertGroup(storeNameStub, addressStub, "", dateStub);
        Mockito.verify(this.groupMapper).updateGroupOfUser(1,3);
    }

    /**
     * createJoinRequest role不正确测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF")
    public void should_access_denied_when_join_with_bad_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.createJoinRequest(3);
        });
    }

    /**
     * createJoinRequest 参数不正确测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_DEFAULT", groupId = 2)
    public void should_throws_when_join_with_exist_group_id() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.createJoinRequest(3);
        });
    }

    /**
     * createJoinRequest测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_DEFAULT")
    public void should_call_insert_request() {
        this.groupService.createJoinRequest(3);
        verify(this.groupMapper).insertRequest(1, 3);
    }

    /**
     * approveJoinGroupRequest参数错误测试 - OWNER身份(合法)
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 2)
    public void should_get_access_denied_when_update_with_default_role() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(0, List.of());
        });
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(2,null);
        });

        // 1. user不存在
        when(this.groupMapper.getGroupByUserId(1)).thenReturn(new Group.Builder().id(2).build());
        when(this.userService.getUserByID(2)).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(2, List.of());
        });

        // 2. user已经有group
        when(this.userService.getUserByID(2)).thenReturn(new User.Builder().group(new Group.Builder().id(1).build()).build()); // target user 的group id为0
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(2, List.of());
        });
    }

    /**
     * approveJoinGroupRequest正常测试 - OWNER身份(合法)
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 2)
    public void should_update_when_update_with_default_role() {
        when(this.groupMapper.getGroupByUserId(1)).thenReturn(new Group.Builder().id(2).build());
        when(this.userService.getUserByID(2)).thenReturn(new User("approve_test", "test001")); // target user 的group id为0

        List<SystemAuthority> permission = List.of(
                new SystemAuthority(SystemAuthority.Permission.STATISTICS.value()),
                new SystemAuthority(SystemAuthority.Permission.SHOPPING.value()),
                new SystemAuthority("not_valid_permssion")
                );

        this.groupService.approveJoinGroupRequest(2, permission);

        verify(this.groupMapper).updateGroupOfUser(2,2);
        verify(this.authorityMapper).updateRole(2,SystemAuthority.Role.STAFF.value());
        verify(this.authorityMapper, times(2)).insertAuthority(anyInt(), any());
        verify(this.groupMapper).deleteRequest(2);
        verify(this.userCacheService).removeUserFromCache("approve_test");
    }

    /**
     * 权限错误时deleteUserInGroup测试 - default role
     */
    @Test
    @WithMockUserPrincipal
    public void should_throw_access_denied_when_delete_user_in_group() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.deleteUserInGroup(3);
        });
    }

    /**
     * deleteUserInGroup测试 - default role
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 2)
    public void should_throw_or_delete_when_delete_user_in_group() {
        int userId = 3;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.deleteUserInGroup(userId);
        });

        User user = new User.Builder().id(userId).group(new Group.Builder().id(3).build()).build();
        when(this.userService.getUserByID(userId)).thenReturn(user);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.deleteUserInGroup(userId);
        });

        user = new User.Builder().id(userId).username("delete_test").group(new Group.Builder().id(2).build()).build();
        when(this.userService.getUserByID(userId)).thenReturn(user);
        this.groupService.deleteUserInGroup(userId);
        verify(this.groupMapper).updateGroupOfUser(userId, 0);
        verify(this.authorityMapper).updateRole(userId, SystemAuthority.Role.DEFAULT.value());
        verify(this.authorityMapper).deletePermission(userId, SystemAuthority.Permission.SHOPPING.value());
        verify(this.authorityMapper).deletePermission(userId, SystemAuthority.Permission.INVENTORY.value());
        verify(this.authorityMapper).deletePermission(userId, SystemAuthority.Permission.STATISTICS.value());
        verify(this.userCacheService).removeUserFromCache("delete_test");
    }


    /**
     * 权限错误时updateStoreName updateAddress updateContact测试 - default role
     */
    @Test
    @WithMockUserPrincipal
    public void should_throw_access_denied_when_update_with_default() {
        String storeNameStub = "test_store";
        String addressStub = "test_address";
        String contactStub = "test_contact";

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.updateStoreName(storeNameStub);
        });
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.updateAddress(addressStub);
        });
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.updateStoreName(contactStub);
        });
    }

    /**
     * 权限错误时updateStoreName updateAddress updateContact测试 - default role
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF")
    public void should_throw_access_denied_when_update_with_staff() {
        String storeNameStub = "test_store";
        String addressStub = "test_address";
        String contactStub = "test_contact";

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.updateStoreName(storeNameStub);
        });
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.updateAddress(addressStub);
        });
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.updateStoreName(contactStub);
        });
    }

    /**
     * 权限错误时updateStoreName updateAddress updateContact测试 - default role
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_throw_or_update_with_owner() {
        String storeNameStub = "test_store";
        String addressStub = "test_address";
        String contactStub = "test_contact";

        when(this.groupMapper.getGroupByUserId(1)).thenReturn(new Group.Builder().id(0).build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.updateStoreName(storeNameStub);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.updateAddress(addressStub);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.updateContact(contactStub);
        });

        when(this.groupMapper.getGroupByUserId(1)).thenReturn(new Group.Builder().id(1).build());
        this.groupService.updateStoreName(storeNameStub);
        this.groupService.updateAddress(addressStub);
        this.groupService.updateContact(contactStub);
        verify(this.groupMapper).updateStoreName(1 ,storeNameStub);
        verify(this.groupMapper).updateAddress(1 ,addressStub);
        verify(this.groupMapper).updateContact(1 ,contactStub);
    }

    /**
     * 权限错误时getUsersInGroup测试- default role
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_users_with_default() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.getUsersInGroup();
        });
    }

    /**
     * detUsersInGroup测试- default OWNER
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 3)
    public void should_access_denied_when_get_users_with_owner() {
        this.groupService.getUsersInGroup();
        verify(this.groupMapper).getUsersByGroupId(3);
    }

    /**
     * deleteRequest 角色错误测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER")
    public void should_access_denied_when_delete_request_without_user_id() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.deleteRequest();
        });

        // 未找到group
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.deleteRequest(3);
        });

        // 对group没有权限
        when(this.groupMapper.getGroupByUserIdInRequest(3)).thenReturn(new Group.Builder().id(2).build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.groupService.deleteRequest(3);
        });
    }

    /**
     * deleteRequest 角色错误测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF")
    public void should_access_denied_when_delete_request_with_staff() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.deleteRequest();
        });

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.deleteRequest(3);
        });
    }

    /**
     * deleteRequest 测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_delete_request() {
        this.groupService.deleteRequest();
        verify(this.groupMapper).deleteRequest(1);
    }

    /**
     * deleteRequest 测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", id = 3, groupId = 3)
    public void should_delete_request_with_id() {
        when(this.groupMapper.getGroupByUserIdInRequest(6)).thenReturn(new Group.Builder().id(3).build());
        this.groupService.deleteRequest(6);
        verify(this.groupMapper).deleteRequest(6);
    }

    /**
     * getUsersUnderRequest 角色错误测试 - default role
     */
    @Test
    @WithMockUserPrincipal()
    public void should_access_denied_when_get_users_under_request_with_default() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.getUsersUnderRequest();
        });
    }

    /**
     * getUsersUnderRequest 角色错误测试 - staff role
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF")
    public void should_access_denied_when_get_users_under_request_with_staff() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.groupService.getUsersUnderRequest();
        });
    }

    /**
     * getUsersUnderRequest测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 8)
    public void should_call_mapper_when_get_users_under_request() {
        List<UserProfile> userProfiles = new ArrayList<>(2);
        when(this.groupMapper.getRequestUsersByGroupId(8)).thenReturn(userProfiles);

        List<UserProfile> result = this.groupService.getUsersUnderRequest();
        assertThat(result, is(userProfiles));
        verify(this.groupMapper).getRequestUsersByGroupId(8);
    }
}

