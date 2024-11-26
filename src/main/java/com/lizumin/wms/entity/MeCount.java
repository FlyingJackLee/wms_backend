package com.lizumin.wms.entity;

public class MeCount {
    private Category category;
    private int count;
    private double sumCost;
    private double sumPrice;

    public void setCount(int count) {
        this.count = count;
    }

    public void setSumCost(double sumCost) {
        this.sumCost = sumCost;
    }

    public void setSumPrice(double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public double getSumCost() {
        return sumCost;
    }

    public double getSumPrice() {
        return sumPrice;
    }
    public Category getCategory() {
        return category;
    }

    public void add(MeCount meCount) {
        this.count += meCount.count;
        this.sumCost += meCount.sumCost;
        this.sumPrice += meCount.sumPrice;
    }
}
