package com.lizumin.wms.service;

import com.lizumin.wms.dao.OrderMapper;
import com.lizumin.wms.entity.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/1 15:40
 */
@Service
public class OrderService extends AbstractAuthenticationService {
    private final OrderMapper orderMapper;
    private final MerchandiseService merchandiseService;

    public OrderService(OrderMapper orderMapper, MerchandiseService merchandiseService) {
        this.orderMapper = orderMapper;
        this.merchandiseService = merchandiseService;
    }

    /**
     * 销售订单创建
     *
     * @param meId
     * @param sellingPrice
     * @param date
     * @param remark
     * @return
     */
    @Transactional
    @PreAuthorize("hasRole('STAFF')")
    public int insertOrder(int meId, double sellingPrice, String remark, Date date) {
        Assert.isTrue(meId > 0, "invalid id");
        Assert.isTrue(checkMePermission(meId), "dont have permission");

        int id = this.orderMapper.insertOrder(meId, sellingPrice, remark, date, getUserId(), getGroupId()); // 创建订单
        this.merchandiseService.updateSold(meId, true); // 更新商品销售状态
        return id;
    }

    /**
     * 批量插入order
     *
     * @param orders
     */
    @Transactional
    @PreAuthorize("hasRole('STAFF')")
    public void insertOrder(List<Order> orders) {
        for (Order order: orders){
            this.insertOrder(order.getMerchandise().getId(), order.getSellingPrice(), order.getRemark(), order.getSellingTime());
        }
    }

    /**
     * 查询日期范围内的订单
     *
     * @param sellingTimeStart
     * @param sellingTimeEnd
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public List<Order> getOrdersByDateRange(Date sellingTimeStart, Date sellingTimeEnd) {
        long diff = sellingTimeEnd.getTime() - sellingTimeStart.getTime();
        Assert.isTrue(diff <= 31708800000L, "limit date range in 1 year"); // 范围不能超过367天
        return this.orderMapper.getOrdersByDateRange(getGroupId(), sellingTimeStart, sellingTimeEnd);
    }

    /**
     * 退货设置
     *
     * @param orderId
     */
    @Transactional
    @PreAuthorize("hasRole('STAFF')")
    public void returnOrder(int orderId) {
        Assert.isTrue(orderId > 0, "invalid order id");
        // 商品设置为未销售
        this.merchandiseService.updateSold(this.orderMapper.getOrderById(orderId, getGroupId()).getMerchandise().getId(), false);
        this.orderMapper.setOrderReturned(orderId, true, getGroupId());
    }

    private boolean checkMePermission(int meId) {
        return this.merchandiseService.getMerchandiseById( meId) != null;
    }
}
