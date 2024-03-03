package com.lizumin.wms.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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

    private String remark; //备注

    private Date sellingTime; //销售日期

    public Order() {
    }

    public Order(int id, double sellingPrice, boolean returned, String remark, Date sellingTime, Merchandise merchandise) {
        this.id = id;
        this.merchandise = merchandise;
        this.sellingPrice = sellingPrice;
        this.returned = returned;
        this.remark = remark;
        this.sellingTime = sellingTime;
    }

    public Merchandise getMerchandise() {
        return merchandise;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public boolean isReturned() {
        return returned;
    }

    public String getRemark() {
        return remark;
    }

    public Date getSellingTime() {
        return sellingTime;
    }

    public void setMerchandise(Merchandise merchandise) {
        this.merchandise = merchandise;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSellingTime(Date sellingTime) {
        this.sellingTime = sellingTime;
    }

    public int getId() {
        return id;
    }
}
