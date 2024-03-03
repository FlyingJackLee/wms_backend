package com.lizumin.wms.entity;

import java.util.Objects;

/**
 * 商品分类
 *
 * @author Zumin Li
 * @date 2024/2/10 20:37
 */
public class Category {
    private int id;
    private int parentId; // 父分类id，用于多级分类，0表示一级分类
    private String name;

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id && parentId == category.parentId && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, name);
    }
}
