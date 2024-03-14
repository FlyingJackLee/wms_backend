package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

@Mapper
public interface UserMapper {
    User getUserByUsername(@Param("username") String username);

    User getUserById(@Param("user_id") int id);

    String getUsernameByEmail(@Param("email") String email);
    String getUsernameByPhoneNumber(@Param("phone_number") String phoneNumber);
    boolean isUsernameExist(@Param("username") String username);
    boolean isPhoneExist(@Param("phone") String phone);
    boolean isEmailExist(@Param("email") String email);
    // 用户已存在抛出DuplicateKeyException，字段不合法抛出DataIntegrityViolationException
    void insertUser(User user) throws DuplicateKeyException, DataIntegrityViolationException;

    // insert OR !UPDATE! email must be a valid email
    void updateEmail(@Param("user_id") int user_id, @Param("email") String email) throws DuplicateKeyException, DataIntegrityViolationException;

    // phone must be a valid email
    void updatePhone(@Param("user_id") int user_id, @Param("phone") String phone) throws DuplicateKeyException, DataIntegrityViolationException;

    // password must be encrypted!
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);
}
