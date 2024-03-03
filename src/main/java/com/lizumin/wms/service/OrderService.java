package com.lizumin.wms.service;

import com.lizumin.wms.dao.OrderMapper;
import com.lizumin.wms.entity.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * @param authentication
     * @param meId
     * @param sellingPrice
     * @param date
     * @param remark
     * @return
     */
    @Transactional
    public int insertOrder(Authentication authentication, int meId, double sellingPrice, String remark, Date date) {
        Assert.isTrue(meId > 0, "invalid id");
        Assert.isTrue(checkMePermission(authentication, meId), "dont have permission");

        int ownId = getOwnerId(authentication);
        int id = this.orderMapper.insertOrder(meId, sellingPrice, remark, date, ownId); // 创建订单
        this.merchandiseService.updateSold(authentication, meId, true); // 更新商品销售状态
        return id;
    }

    /**
     * 批量插入order
     *
     * @param authentication
     * @param orders
     */
    @Transactional
    public void insertOrder(Authentication authentication, List<Order> orders) {
        for (Order order: orders){
            this.insertOrder(authentication, order.getMerchandise().getId(), order.getSellingPrice(), order.getRemark(), order.getSellingTime());
        }
    }

    /**
     * 查询日期范围内的订单
     *
     * @param authentication
     * @param sellingTimeStart
     * @param sellingTimeEnd
     * @return
     */
    public List<Order> getOrdersByDateRange(Authentication authentication, Date sellingTimeStart, Date sellingTimeEnd) {
        long diff = sellingTimeEnd.getTime() - sellingTimeStart.getTime();
        Assert.isTrue(diff <= 31708800000L, "limit date range in 1 year"); // 范围不能超过367天
        return this.orderMapper.getOrdersByDateRange(getOwnerId(authentication), sellingTimeStart, sellingTimeEnd);
    }

    /**
     * 退货设置
     *
     * @param authentication
     * @param orderId
     */
    @Transactional
    public void returnOrder(Authentication authentication, int orderId) {
        Assert.isTrue(orderId > 0, "invalid order id");
        // 商品设置为未销售
        this.merchandiseService.updateSold(authentication, this.orderMapper.getOrderById(orderId, getOwnerId(authentication)).getMerchandise().getId(), false);
        this.orderMapper.setOrderReturned(orderId, true, getOwnerId(authentication));
    }

    private boolean checkMePermission(Authentication authentication,  int meId) {
        return this.merchandiseService.getMerchandiseById(authentication, meId) != null;
    }
}
