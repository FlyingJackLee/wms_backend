package com.lizumin.wms.dao;

import com.lizumin.wms.entity.User;
import com.lizumin.wms.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

@Mapper
public interface UserMapper {
    User getUserByUsername(@Param("username") String username);

    User getUserById(@Param("user_id") int id);

    String getUsernameByEmail(@Param("email") String email);
    String getUsernameByPhoneNumber(@Param("phone_number") String phoneNumber);

    /**
     * 根据手机号码查询id
     * @param phoneNumber 手机号
     * @return 注意返回null表示未查询到
     */
    Integer getGroupIdByPhone(@Param("phone_number") String phoneNumber);

    boolean isUsernameExist(@Param("username") String username);
    boolean isPhoneExist(@Param("phone") String phone);
    boolean isEmailExist(@Param("email") String email);
    // 用户已存在抛出DuplicateKeyException，字段不合法抛出DataIntegrityViolationException
    void insertUser(User user) throws DuplicateKeyException, DataIntegrityViolationException;

    // password must be encrypted!
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    // password must be encrypted!
    void updatePasswordByPhone(@Param("phone") String phone, @Param("password") String password);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserProfile getProfile(@Param("user_id") int userId);

    /**
     * insert OR !UPDATE! email must be a valid email
     */
    void updateEmail(@Param("user_id") int userId, @Param("email") String email) throws DuplicateKeyException, DataIntegrityViolationException;

    // phone must be a valid email

    /**
     * phone must be valid
     *
     */
    void updatePhone(@Param("user_id") int userId, @Param("phone") String phone) throws DuplicateKeyException, DataIntegrityViolationException;


    /**
     * 更新用户显示名
     */
    void updateNickname(@Param("user_id") int userId, @Param("nickname") String nickname);
}
