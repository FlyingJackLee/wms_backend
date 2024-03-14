package com.lizumin.wms.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SystemAuthority implements GrantedAuthority {
    public enum Role {
        ADMIN("ROLE_ADMIN"), OWNER("ROLE_OWNER"), STAFF("ROLE_STAFF"), DEFAULT("ROLE_DEFAULT");

        private String name;
        private Role(String role) {
            this.name = role;
        }

        public String value(){
            return this.name;
        }

        @Override
        public String toString() {
            return this.value();
        }
    }

    public enum Permission {
        SHOPPING("PERMISSION:shopping"), INVENTORY("PERMISSION:inventory"), STATISTICS("PERMISSION:statistic");

        private String name;
        private Permission(String role) {
            this.name = role;
        }

        public String value(){
            return this.name;
        }

        @Override
        public String toString() {
            return this.value();
        }
    }

    private static final long serialVersionUID = 500L;

    private String authority;

    public SystemAuthority() {}

    public SystemAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * 管理员权限，拥有所有系统的读写删权限
     *
     */
    public static Set<GrantedAuthority> owner(){
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SystemAuthority(Role.OWNER.value()));
        return authorities;
    }

    /**
     * 员工权限，需要添加permission权限访问
     *
     */
    public static Set<GrantedAuthority> staff(){
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SystemAuthority(Role.STAFF.value()));
        return authorities;
    }

    /**
     * 默认权限，注册后所在权限，加入企业后变更为其他
     *
     */
    public static Set<GrantedAuthority> defaults(){
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SystemAuthority(Role.DEFAULT.value()));
        return authorities;
    }

    /**
     * 检查传入的permission是否合法
     *
     * @param permission
     * @return
     */
    public static boolean isValidPermission(SystemAuthority permission) {
        return isValidPermission(permission.getAuthority());
    }

    public static boolean isValidPermission(String permission) {
        return permission.equals(Permission.SHOPPING.value()) ||
                permission.equals(Permission.INVENTORY.value()) ||
                permission.equals(Permission.STATISTICS.value());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemAuthority that = (SystemAuthority) o;
        return Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }
}
