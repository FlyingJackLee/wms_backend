package com.lizumin.wms.service;

import com.lizumin.wms.dao.AuthorityMapper;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/25 12:59
 */
@Service
public class AuthorityService extends AbstractAuthenticationService {
    private AuthorityMapper authorityMapper;
    private UserService userService;

    private UserCacheService userCacheService;

    public AuthorityService(AuthorityMapper authorityMapper, UserService userService, UserCacheService userCacheService) {
        this.authorityMapper = authorityMapper;
        this.userService = userService;
        this.userCacheService = userCacheService;
    }

    /**
     * 获取当前用户role
     */
    public SystemAuthority getRole() {
        return this.authorityMapper.getRole(getUserId());
    }

    /**
     * 获取当前用户权限
     */
    public List<SystemAuthority> getPermission() {
        return this.authorityMapper.getPermissions(getUserId());
    }

    /**
     * 获取用户的权限, 只能是owner查询同一组下的
     */
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @Transactional
    public List<SystemAuthority> getPermissionsByUserId(int userId) {
        Assert.isTrue(this.userService.getUserByID(userId).getGroup().getId() == getUser().getGroup().getId(),
                "");
        return this.authorityMapper.getPermissions(userId);
    }

    /**
     * 更新用户权限
     *
     * @param userId 用户id
     * @param shopping 收银模块权限
     * @param inventory 库存管理权限
     * @param statistics 订单查询权限
     */
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @Transactional
    public void updatePermission(int userId, boolean shopping, boolean inventory, boolean statistics) {
        User user = this.userService.getUserByID(userId);
        Assert.isTrue(user != null && user.getGroup() != null, "invalid user id");
        Assert.isTrue(user.getGroup().getId() == getUser().getGroup().getId(), "not in same group");

        if (shopping) {
            this.authorityMapper.insertAuthority(userId, SystemAuthority.Permission.SHOPPING.value());
        } else {
            this.authorityMapper.deletePermission(userId, SystemAuthority.Permission.SHOPPING.value());
        }

        if (inventory) {
            this.authorityMapper.insertAuthority(userId, SystemAuthority.Permission.INVENTORY.value());
        } else {
            this.authorityMapper.deletePermission(userId, SystemAuthority.Permission.INVENTORY.value());
        }

        if (statistics) {
            this.authorityMapper.insertAuthority(userId, SystemAuthority.Permission.STATISTICS.value());
        } else {
            this.authorityMapper.deletePermission(userId, SystemAuthority.Permission.STATISTICS.value());
        }

        // 删除User缓存 - 以保证后续操作获取获取到最新User
        this.userCacheService.removeUserFromCache(user.getUsername());
    }
}
