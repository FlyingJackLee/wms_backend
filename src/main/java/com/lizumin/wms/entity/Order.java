package com.lizumin.wms.entity;

/**
 * 订单
 *
 * @author Zumin Li
 * @date 2024/2/10 20:42
 */
public class Order {
    private int id;
    private Merchandise merchandise;
    private double sellingPrice; // 销售价格
    private boolean returned; // 是否退货
}
