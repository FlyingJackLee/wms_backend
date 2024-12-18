package com.lizumin.wms.service;

import com.lizumin.wms.dao.AuthorityMapper;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.exception.AuthenticationUserException;
import com.lizumin.wms.dao.UserMapper;

import com.lizumin.wms.exception.UserNotFoundException;
import com.lizumin.wms.tool.Verify;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class UserService implements UserDetailsService {
    private final UserMapper userMapper;
    private final UserCache userCache;

    private PasswordEncoder passwordEncoder;

    private AuthorityMapper authorityMapper;

    public UserService(UserMapper userMapper, UserCache userCache, PasswordEncoder passwordEncoder, AuthorityMapper authorityMapper) {
        this.userMapper = userMapper;
        this.userCache = userCache;
        this.passwordEncoder = passwordEncoder;
        this.authorityMapper = authorityMapper;
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
     * @param username: not null or empty
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
     * 根据id获取User
     *
     * @param id
     * @return
     */
    public User getUserByID(int id) {
        Assert.isTrue(id > 0, "invalid user id");
        return this.userMapper.getUserById(id);
    }

    /**
     * Get username by email
     *
     * @param email: not null or empty
     * @return
     */
    public String getUsernameByEmail(String email) {
        return this.userMapper.getUsernameByEmail(email);
    }

    /**
     * Get username by phone number
     *
     * @param phoneNumber: not null or empty
     * @return
     */
    public String getUsernameByPhoneNumber(String phoneNumber) {
        return this.userMapper.getUsernameByPhoneNumber(phoneNumber);
    }

    /**
     * get group id by phone
     *
     * @param phone
     * @return
     */
    public int getGroupIdByPhone(String phone){
        Assert.isTrue(Verify.verifyPhoneNumber(phone), "not a valid phone");
        Integer groupId = this.userMapper.getGroupIdByPhone(phone);

        // 为null表示未找到返回-1
        if (groupId == null) {
            return -1;
        } else {
            return (int) groupId;
        }
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
     * @return true: email exist vice versa
     */
    public boolean isEmailExist(String email) {
        return this.userMapper.isEmailExist(email);
    }

    /**
     * 插入user并返回id
     *
     * @param user not null or empty
     * @return
     */
    @Transactional
    public int insertUser(User user) {
        this.userMapper.insertUser(user);
        if (user.getId() >= 2) {
            user.getAuthorities().forEach(authority -> {
                this.authorityMapper.insertAuthority(user.getId(), authority.getAuthority());
            });
        }
        return user.getId();
    }

    /**
     * 插入user及其邮箱手机
     *
     * @param user not null or empty
     * @param email not null or empty
     * @param phone not null or empty
     * @return
     */
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

    /**
     * 根据邮箱重置密码
     *
     * @param email not null or empty
     * @param password not null or empty
     * @return
     */
    public boolean resetPasswordByEmail(@NonNull String email, @Nullable String password) {
        // 加密密钥
        String encryptedPassword = passwordEncoder.encode(password);
        try {
            this.userMapper.updatePasswordByEmail(email, encryptedPassword);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 根据手机号重置密码
     *
     * @param phone not null or empty
     * @param password not null or empty
     * @return
     */
    public boolean resetPasswordByPhone(@NonNull String phone, @Nullable String password) {
        // 加密密钥
        String encryptedPassword = passwordEncoder.encode(password);
        try {
            this.userMapper.updatePasswordByPhone(phone, encryptedPassword);
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
