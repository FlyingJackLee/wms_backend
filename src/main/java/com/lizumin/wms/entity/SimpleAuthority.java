package com.lizumin.wms.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class SimpleAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 500L;

    private String authority;

    public SimpleAuthority() {}

    public SimpleAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public static GrantedAuthority userAuthority() {
        return new SimpleAuthority("ROLE_USER");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleAuthority that = (SimpleAuthority) o;
        return Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }
}
