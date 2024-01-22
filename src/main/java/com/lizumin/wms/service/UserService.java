package com.lizumin.wms.service;

import com.lizumin.wms.entity.User;
import com.lizumin.wms.exception.AuthenticationUserException;
import com.lizumin.wms.dao.UserMapper;

import com.lizumin.wms.exception.UserNotFoundException;
import com.lizumin.wms.tool.Verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserMapper userMapper;
    private final UserCache userCache;

    @Autowired
    public UserService(UserMapper userMapper, UserCache userCache) {
        this.userMapper = userMapper;
        this.userCache = userCache;
    }

    /**
     * Get user by username
     *
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!Verify.isNotBlank(username)) {
            throw new AuthenticationUserException("BPV-000");
        }
        User user = this.userMapper.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("BPV-004");
        }
        return user;
    }

    /**
     * Get user from cache or database.
     *
     * @param username
     * @return
     */
    public User lazyLoadUserByUsername(String username) {
        UserDetails user =  this.userCache.getUserFromCache(username);
        // if no user in cache, get from database
        if (user == null) {
            user = this.loadUserByUsername(username);
            this.userCache.putUserInCache(user);
        }
        return (User) user;
    }

    /**
     * Get username by email
     *
     * @param email
     * @return
     */
    public String getUsernameByEmail(String email) {
        return this.userMapper.getUsernameByEmail(email);
    }

    /**
     * Get username by phone number
     *
     * @param phoneNumber
     * @return
     */
    public String getUsernameByPhoneNumber(String phoneNumber) {
        return this.userMapper.getUsernameByPhoneNumber(phoneNumber);
    }

    /**
     * Check if username has been used
     *
     * @param username: not null
     * @return true: username exist vice versa
     */
    public boolean isUsernameExist(String username) {
        return this.userMapper.isUsernameExist(username);
    }

    /**
     * Check if Phone has been used
     *
     * @param phone: not null and legal
     * @return true: phone exist vice versa
     */
    public boolean isPhoneExist(String phone) {
        return this.userMapper.isPhoneExist(phone);
    }

    /**
     * Check if email has been used
     *
     * @param email: not null and legal
     * @return  true: email exist vice versa
     */
    public boolean isEmailExist(String email) {
        return this.userMapper.isEmailExist(email);
    }

    /**
     * 插入user并返回id
     *
     * @param user
     * @return
     */

    @Transactional
    public int insertUser(User user) {
        this.userMapper.insertUser(user);
        if (user.getId() >= 2) {
            user.getAuthorities().forEach(authority -> {
                this.userMapper.insertAuthority(user.getId(), authority.getAuthority());
            });
        }
        return user.getId();
    }

    @Transactional
    public int insertUser(User user, @Nullable String email, @Nullable String phone) {
        int id = insertUser(user);
        if (id >= 2 && email != null) {
            userMapper.updateEmail(id, email);
        }
        if (id >= 2 && phone != null) {
            userMapper.updatePhone(id, phone);
        }
        return id;
    }
}
