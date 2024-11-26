package com.lizumin.wms.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

public class User implements UserDetails, CredentialsContainer {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private static final Log logger = LogFactory.getLog(org.springframework.security.core.userdetails.User.class);

    private int id;
    private String password;

    private String username;
    private boolean enabled = true;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private Set<GrantedAuthority> authorities = new HashSet<>(0);

    private Group group = new Group();

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private User(Builder builder) {
        setId(builder.id);
        setPassword(builder.password);
        setUsername(builder.username);
        setEnabled(builder.enabled);
        setAccountNonExpired(builder.accountNonExpired);
        setAccountNonLocked(builder.accountNonLocked);
        setCredentialsNonExpired(builder.credentialsNonExpired);
        setAuthorities(builder.authorities);
        setGroup(builder.group);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return id == that.id && accountNonExpired == that.accountNonExpired && accountNonLocked == that.accountNonLocked && credentialsNonExpired == that.credentialsNonExpired && enabled == that.enabled && Objects.equals(password, that.password) && Objects.equals(username, that.username) && Objects.equals(authorities, that.authorities);
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return this.username.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("Username=").append(this.username).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("Enabled=").append(this.enabled).append(", ");
        sb.append("AccountNonExpired=").append(this.accountNonExpired).append(", ");
        sb.append("CredentialsNonExpired=").append(this.credentialsNonExpired).append(", ");
        sb.append("AccountNonLocked=").append(this.accountNonLocked).append(", ");
        sb.append("Granted Authorities=").append(this.authorities).append("]");
        return sb.toString();
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set. If the authority is null, it is a custom authority and should
            // precede others.
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public static final class Builder {
        private int id;
        private String password;

        private String username;
        private boolean enabled = true;

        private boolean accountNonExpired = true;

        private boolean accountNonLocked = true;

        private boolean credentialsNonExpired = true;
        private Set<GrantedAuthority> authorities = new HashSet<>(0);
        private Group group = new Group();

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder enabled(boolean val) {
            enabled = val;
            return this;
        }

        public Builder accountNonExpired(boolean val) {
            accountNonExpired = val;
            return this;
        }

        public Builder accountNonLocked(boolean val) {
            accountNonLocked = val;
            return this;
        }

        public Builder credentialsNonExpired(boolean val) {
            credentialsNonExpired = val;
            return this;
        }

        public Builder authorities(Set<GrantedAuthority> val) {
            authorities = val;
            return this;
        }

        public Builder group(Group val) {
            group = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
