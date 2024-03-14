package com.lizumin.wms.dao;

import com.lizumin.wms.entity.SystemAuthority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/12 20:07
 */
@Mapper
public interface AuthorityMapper {
    /**
     * 获取账号的角色
     * @param user_id 用户id
     */
    SystemAuthority getRole(@Param("user_id") int user_id);

    /**
     * 更新角色, 不会插入不存在对象
     *
     * @param userId 用户id
     * @param role 角色，请使用SystemAuthority.Role传入
     */
    void updateRole(@Param("user_id") int userId, @Param("role") String role);

    /**
     * 获取账号的权限
     * @param user_id 用户id
     */
    List<SystemAuthority> getPermissions(@Param("user_id") int user_id);

    /**
     * 删除权限(不允许删除role)
     *
     * @param user_id 用户id
     * @param permission 权限， SystemAuthority.Permission
     */
    void deletePermission(@Param("user_id") int user_id, @Param("permission") String permission);

    /**
     * 获取账号的所有授权
     * @param user_id 用户id
     */
    List<SystemAuthority> getAuthorities(@Param("user_id") int user_id);

    /**
     * 如果不存在插入权限信息
     *
     * @param user_id 用户id
     * @param authority 权限 Role或者Permission
     */
    void insertAuthority(@Param("user_id") int user_id, @Param("authority") String authority);
}
