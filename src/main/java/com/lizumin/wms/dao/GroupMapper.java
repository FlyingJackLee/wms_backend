package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;
import java.util.List;

@Mapper
public interface GroupMapper {
    /**
     * 获取user所属group
     *
     * @param userId
     * @return
     */
    Group getGroupByUserId(@Param("user_id") int userId);

    /**
     * 更新归属组
     *
     * @param groupId 组 id 必须存在
     */
    void updateGroupOfUser(@Param("user_id") int userId, @Param("group_id") int groupId);

    /**
     * 插入group, 对Group有side effect
     *
     */
    int insertGroup(@Param("store_name") String storeName, @Param("address") String address, @Param("contact") String contact, @Param("create_time") Date createTime);

    /**
     * 根据id获取group
     * @param groupId
     * @return
     */
    Group getGroupById(@Param("group_id") int groupId);

    /**
     * 修改商铺名
     *
     * @param groupId  group id
     * @param storeName 店名
     */
    void updateStoreName(@Param("group_id") int groupId, @Param("store_name") String storeName);

    /**
     * 更改商铺地址
     *
     * @param groupId group id
     * @param address 地址
     */
    void updateAddress(@Param("group_id") int groupId, @Param("address") String address);

    /**
     * 更改商铺联系电话
     *
     * @param groupId group id
     * @param contact 联系电话
     */
    void updateContact(@Param("group_id") int groupId, @Param("contact") String contact);

    /**
     * 获取group下所有user(保密性要求，只获取id，nickname，phone，email)
     *
     * @param groupId
     * @return
     */
    List<UserProfile> getUsersByGroupId(@Param("group_id") int groupId);

    // 下面为group_request表操作
    /**
     * 加入group请求 - 操作表为group_request
     *
     * @param userId
     * @param groupId
     * DataIntegrityViolationException group不存在
     */
    void insertRequest(@Param("user_id") int userId, @Param("group_id") int groupId) throws DataIntegrityViolationException;

    /**
     * 删除加入group请求
     *
     * @param userId
     */
    void deleteRequest(@Param("user_id") int userId);

    /**
     * 获取user申请group信息
     *
     * @param userId
     * @return
     */
    Group getGroupByUserIdInRequest(@Param("user_id") int userId);

    /**
     * 获取group下的所有申请
     *
     * @param groupId
     * @return
     */
    List<UserProfile> getRequestUsersByGroupId(@Param("group_id") int groupId);
}
