package com.lizumin.wms.service;

import com.lizumin.wms.dao.AuthorityMapper;
import com.lizumin.wms.dao.GroupMapper;
import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
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
        Mockito.verify(this.groupMapper).updateGroup(1,3);
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
        Mockito.verify(this.groupMapper).updateGroup(1,3);
    }

    /**
     * approveJoinGroupRequest参数错误测试 - OWNER身份(合法)
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 2)
    public void should_get_access_denied_when_update_with_default_role() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(0, 0, List.of());
        });
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(2, 2, null);
        });

        // 1. 无权限
        when(this.groupMapper.getGroupByUserId(1)).thenReturn(new Group.Builder().id(1).build());
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(2, 2, List.of());
        });

        // 2. user不存在
        when(this.groupMapper.getGroupByUserId(1)).thenReturn(new Group.Builder().id(2).build());
        when(this.userService.getUserByID(2)).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(2, 2, List.of());
        });

        // 3. user已经有group
        when(this.userService.getUserByID(2)).thenReturn(new User.Builder().group(new Group.Builder().id(1).build()).build()); // target user 的group id为0
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            this.groupService.approveJoinGroupRequest(2, 2, List.of());
        });
    }

    /**
     * approveJoinGroupRequest正常测试 - OWNER身份(合法)
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 2)
    public void should_update_when_update_with_default_role() {
        when(this.groupMapper.getGroupByUserId(1)).thenReturn(new Group.Builder().id(2).build());
        when(this.userService.getUserByID(2)).thenReturn(new User()); // target user 的group id为0

        List<SystemAuthority> permission = List.of(
                new SystemAuthority(SystemAuthority.Permission.STATISTICS.value()),
                new SystemAuthority(SystemAuthority.Permission.SHOPPING.value()),
                new SystemAuthority("not_valid_permssion")
                );

        this.groupService.approveJoinGroupRequest(2, 2, permission);

        verify(this.groupMapper).updateGroup(2,2);
        verify(this.authorityMapper).updateRole(2,SystemAuthority.Role.STAFF.value());
        verify(this.authorityMapper, times(2)).insertAuthority(anyInt(), any());
        verify(this.groupMapper).deleteRequest(2);
    }

}
