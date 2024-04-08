package com.lizumin.wms.service;

import com.lizumin.wms.dao.AuthorityMapper;
import com.lizumin.wms.dao.GroupMapper;
import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.entity.UserProfile;
import com.lizumin.wms.tool.Verify;
import org.springframework.dao.DataIntegrityViolationException;
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
     * 为当前用户新建用户组
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
        Assert.isTrue(getUser().getGroup().getId() == 0, "data integrity error: group id should be 0");

        // 1. 新建group
        Assert.isTrue(Verify.isNotBlank(storeName), "store name cannot be empty");
        address = address == null ? "" : address;
        contact = contact == null ? "" : contact;
        createTime = createTime == null ? new Date() : createTime;
        int groupId = this.groupMapper.insertGroup(storeName, address, contact, createTime);

        // 2. 更新归属 和权限
        Assert.isTrue(groupId > 0, "Group insert error");
        this.groupMapper.updateGroupOfUser(getUserId(), groupId);
        this.authorityMapper.updateRole(getUserId(), SystemAuthority.Role.OWNER.value()); // 创建者默认就是拥有者

        // 3. 删除User缓存 - 以保证后续操作获取获取到最新User
        this.userCacheService.removeUserFromCache(getUser().getUsername());
    }

    /**
     * 当前用户发起加入申请
     *
     * @param groupId 加入的groupId
     *
     * @throws DataIntegrityViolationException: 目标group不存在
     */
    @PreAuthorize("hasRole('DEFAULT')")
    public void createJoinRequest(int groupId) throws DataIntegrityViolationException {
        Assert.isTrue(getUserId() > 0 && groupId > 0, "invalid user id or group id");
        Assert.isTrue(getUser().getGroup().getId() == 0, "user already have a group"); // 不允许加入多个group

        this.groupMapper.insertRequest(getUserId(), groupId);
    }

    /**
     * 同意加入请求 - 只能是拥有者才能同意
     *
     */
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void approveJoinGroupRequest(int userId, List<SystemAuthority> permissions) {
        Assert.isTrue(userId > 0 && permissions != null, "invalid user id or group id");

        // 2. 检查目标user 是否已经有非默认值的group，防止错误修改
        User user = this.userService.getUserByID(userId);
        Assert.notNull(user, "cannot find user");
        Assert.isTrue(user.getGroup().getId() == 0, "target already have a group, exist before update");

        // 3. 开始更新组
        this.groupMapper.updateGroupOfUser(userId, getUser().getGroup().getId());

        // 4. 更新role - 设置为员工组
        this.authorityMapper.updateRole(userId, SystemAuthority.Role.STAFF.value());

        // 5. 更新权限组 - 先检查permission是否合法，再进行插入
        permissions.stream().filter(SystemAuthority::isValidPermission).forEach(permission -> {
            this.authorityMapper.insertAuthority(userId, permission.getAuthority());
        });

        // 6.清除相关请求
        this.groupMapper.deleteRequest(userId);

        // 7.删除User缓存 - 以保证后续操作获取获取到最新User
        this.userCacheService.removeUserFromCache(user.getUsername());
    }

    /**
     * 将用户移除组
     *
     * @param userId 被移除的用户id
     */
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void deleteUserInGroup(int userId) {
        User user = this.userService.getUserByID(userId);
        Assert.isTrue(user != null && user.getGroup() != null, "invalid user id");
        Assert.isTrue(user.getGroup().getId() == getUser().getGroup().getId(), "not in same group");

        // 1.变更为归属组 2. 变更角色 3. 删除权限
        this.groupMapper.updateGroupOfUser(userId, 0);
        this.authorityMapper.updateRole(userId, SystemAuthority.Role.DEFAULT.value());
        this.authorityMapper.deletePermission(userId, SystemAuthority.Permission.SHOPPING.value());
        this.authorityMapper.deletePermission(userId, SystemAuthority.Permission.INVENTORY.value());
        this.authorityMapper.deletePermission(userId, SystemAuthority.Permission.STATISTICS.value());

        // 2.删除User缓存 - 以保证后续操作获取获取到最新User
        this.userCacheService.removeUserFromCache(user.getUsername());
    }

    /**
     * 获取当前认证用户的group
     *
     */
    public Group getGroupOfCurrentUser() {
        return this.groupMapper.getGroupByUserId(getUserId());
    }

    /**
     * 更新商商店名 - 只能是拥有者修改
     *
     * @param storeName 店名
     */
    @PreAuthorize("hasRole('OWNER')")
    public void updateStoreName(String storeName) {
        Assert.isTrue(Verify.isNotBlank(storeName), "store name cannot be empty");
        int groupId = this.groupMapper.getGroupByUserId(getUserId()).getId();
        Assert.isTrue(groupId > 0, "invalid group");

        this.groupMapper.updateStoreName(groupId, storeName);
    }

    /**
     * 更新商店地址 - 只能是拥有者修改
     *
     * @param address 店名
     */
    @PreAuthorize("hasRole('OWNER')")
    public void updateAddress(String address) {
        Assert.isTrue(Verify.isNotBlank(address), "store name cannot be empty");
        int groupId = this.groupMapper.getGroupByUserId(getUserId()).getId();
        Assert.isTrue(groupId > 0, "invalid group");

        this.groupMapper.updateAddress(groupId, address);
    }

    /**
     * 更新商商联系方式 - 只能是拥有者修改
     *
     * @param contact 店名
     */
    @PreAuthorize("hasRole('OWNER')")
    public void updateContact(String contact) {
        Assert.isTrue(Verify.isNotBlank(contact), "store name cannot be empty");
        int groupId = this.groupMapper.getGroupByUserId(getUserId()).getId();
        Assert.isTrue(groupId > 0, "invalid group");

        this.groupMapper.updateContact(groupId, contact);
    }

    /**
     * 获取拥有group下所有users信息
     */
    @PreAuthorize("hasRole('OWNER')")
    public List<UserProfile> getUsersInGroup() {
        return this.groupMapper.getUsersByGroupId(getUser().getGroup().getId());
    }

    /**
     * 查询当前用户的申请请求
     */
    @PreAuthorize("hasRole('DEFAULT')")
    public Group getGroupInRequestCurrentUser() {
        return this.groupMapper.getGroupByUserIdInRequest(getUserId());
    }

    /**
     * 根据user id删除加入请求
     */
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void deleteRequest(int userId) {
        Group group = this.groupMapper.getGroupByUserIdInRequest(userId);
        Assert.notNull(group, "没有找到资源");
        Assert.isTrue(group.getId() == getUser().getGroup().getId(), "没有权限"); // 防止恶意删除其他组的数据

        this.groupMapper.deleteRequest(userId);
    }

    /**
     * 删除当前用户加入请求
     */
    @PreAuthorize("hasRole('DEFAULT')")
    public void deleteRequest() {
        this.groupMapper.deleteRequest(getUserId());
    }

    /**
     * 获取当前组下的所有申请用户信息
     */
    @PreAuthorize("hasRole('OWNER')")
    public List<UserProfile> getUsersUnderRequest() {
        return this.groupMapper.getRequestUsersByGroupId(getUser().getGroup().getId());
    }
}
