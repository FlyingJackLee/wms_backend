package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    void updateGroup(@Param("user_id") int userId, @Param("group_id") int groupId);

    /**
     * 插入group, 对Group有side effect
     *
     */
    int insertGroup(@Param("store_name") String storeName, @Param("address") String address, @Param("contact") String contact, @Param("create_time") Date createTime);


    // 下面为group_request表操作
    /**
     * 加入group请求 - 操作表为group_request
     *
     * @param userId
     * @param groupId
     */
    void insertRequest(@Param("user_id") int userId, @Param("group_id") int groupId);

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
    List<User> getRequestUsersByGroupId(@Param("group_id") int groupId);
}
