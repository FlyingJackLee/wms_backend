package com.lizumin.wms.service;

import com.lizumin.wms.dao.AuthorityMapper;
import com.lizumin.wms.dao.GroupMapper;
import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.tool.Verify;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/13 16:10
 */
@Service
public class GroupService extends AbstractAuthenticationService{
    private GroupMapper groupMapper;
    private AuthorityMapper authorityMapper;

    private UserService userService;

    private UserCacheService userCacheService;

    public GroupService(GroupMapper groupMapper, AuthorityMapper authorityMapper, UserService userService, UserCacheService userCacheService) {
        this.groupMapper = groupMapper;
        this.authorityMapper = authorityMapper;
        this.userService = userService;
        this.userCacheService = userCacheService;
    }

    /**
     * 新建用户组
     * - 只允许默认组新建group，防止一个账户有多个group
     *
     * @param storeName group名
     * @param address 地址，可以为空
     * @param contact 联系电话： 可以为空
     * @param createTime 创建时间：默认为时间
     */
    @PreAuthorize("hasRole('DEFAULT')")
    @Transactional
    public void createGroup(String storeName, String address, String contact, Date createTime) {
        // 1. 新建group
        Assert.isTrue(Verify.isNotBlank(storeName), "store name cannot be empty");
        address = address == null ? "" : address;
        contact = contact == null ? "" : contact;
        createTime = createTime == null ? new Date() : createTime;
        int groupId = this.groupMapper.insertGroup(storeName, address, contact, createTime);

        // 2. 更新归属
        Assert.isTrue(groupId > 0, "Group insert error");
        this.groupMapper.updateGroup(getUserId(), groupId);

        // 3. 删除User缓存 - 以保证后续操作获取获取到最新User
        this.userCacheService.removeUserFromCache(getUser().getUsername());
    }

    /**
     * 同意加入请求 - 只能是拥有者才能同意
     *
     */
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void approveJoinGroupRequest(int userId, int groupId, List<SystemAuthority> permissions) {
        Assert.isTrue(userId > 0 && groupId > 0 && permissions != null, "invalid user id or group id");

        // 1. 检查当前用户对group有没有权限 防止非法操作
        Assert.isTrue(this.groupMapper.getGroupByUserId(getUserId()).getId() == groupId, "you dont have permission to do this");

        // 2. 检查目标user 是否已经有非默认值的group，防止错误修改
        User user = this.userService.getUserByID(userId);
        Assert.notNull(user, "cannot find user");
        Assert.isTrue(user.getGroup().getId() == 0, "target already have a group, exist before update");

        // 3. 开始更新组
        this.groupMapper.updateGroup(userId, groupId);

        // 4. 更新role - 设置为员工组
        this.authorityMapper.updateRole(userId, SystemAuthority.Role.STAFF.value());

        // 5. 更新权限组 - 先检查permission是否合法，再进行插入
        permissions.stream().filter(SystemAuthority::isValidPermission).forEach(permission -> {
            this.authorityMapper.insertAuthority(userId, permission.getAuthority());
        });

        // 6.清除相关请求
        this.groupMapper.deleteRequest(userId);
    }

    /**
     * 获取当前认证用户的group
     *
     */
    public Group getGroupOfCurrentUser() {
        return this.groupMapper.getGroupByUserId(getUserId());
    }
}
