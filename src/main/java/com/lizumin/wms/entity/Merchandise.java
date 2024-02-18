package com.lizumin.wms.entity;

import java.util.Date;

/**
 * 商品
 *
 * @author Zumin Li
 * @date 2024/2/10 20:38
 */
public class Merchandise {
    private int id;

    private Category category;

    private double cost; // 成本

    private double price; // 设计售价

    private String imei; // 串号

    private Date createTime;

    private boolean sold; // 是否销售

    public Merchandise() {
    }

    public Merchandise(int id, Category category, double cost, String imei, Date createTime, boolean sold) {
        this.id = id;
        this.category = category;
        this.cost = cost;
        this.imei = imei;
        this.createTime = createTime;
        this.sold = sold;
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public double getCost() {
        return cost;
    }

    public double getPrice() {
        return price;
    }

    public String getImei() {
        return imei;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public boolean isSold() {
        return sold;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
