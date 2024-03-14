package com.lizumin.wms.entity;

import org.apache.ibatis.annotations.Param;

import java.util.Objects;

/**
 * @author Zumin Li
 * @date 2024/3/11 22:49
 */
public class Group {
    private int id;
    private String storeName;
    private String address;
    private String contact;

    public Group() {
    }

    private Group(Builder builder) {
        setId(builder.id);
        setStoreName(builder.storeName);
        setAddress(builder.address);
        setContact(builder.contact);
    }

    public int getId() {
        return id;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && Objects.equals(storeName, group.storeName) && Objects.equals(address, group.address) && Objects.equals(contact, group.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeName, address, contact);
    }


    public static final class Builder {
        private int id;
        private String storeName;
        private String address;
        private String contact;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder storeName(String val) {
            storeName = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder contact(String val) {
            contact = val;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }
}
