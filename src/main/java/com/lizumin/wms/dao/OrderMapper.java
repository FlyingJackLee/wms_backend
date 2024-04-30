package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/1 14:51
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     *
     * @param meId
     * @param sellingPrice
     * @param remark
     * @param ownId
     * @return
     */
    int insertOrder(@Param("me_id") int meId, @Param("selling_price") double sellingPrice,
                    @Param("remark") String remark, @Param("selling_time") Date sellingTime,
                    @Param("own_id") int ownId, @Param("group_id") int groupId);

    /**
     * 按照日期范围查询orders
     *
     * @param groupId
     * @param sellingTimeStart
     * @param sellingTimeEnd
     * @return
     */
    List<Order> getOrdersByDateRange(@Param("group_id") int groupId,
                                     @Param("selling_time_start") Date sellingTimeStart,
                                     @Param("selling_time_end") Date sellingTimeEnd);

    /**
     * 通过id查询order
     * @param order_id
     * @param groupId
     * @return
     */
    Order getOrderById(@Param("order_id") int order_id, @Param("group_id") int groupId);

    /**
     * 设置退货状态
     * @param order_id
     * @param returned
     * @param groupId
     */
    void setOrderReturned(@Param("order_id") int order_id, @Param("returned") boolean returned, @Param("group_id") int groupId);
}
