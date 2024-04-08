package com.lizumin.wms.service;

import com.lizumin.wms.dao.UserMapper;
import com.lizumin.wms.entity.UserProfile;
import com.lizumin.wms.tool.Verify;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

/**
 * @author Zumin Li
 * @date 2024/3/16 14:53
 */
@Service
public class ProfileService extends AbstractAuthenticationService{
    private final UserMapper userMapper;

    public ProfileService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 返回当前用户的用户资料
     *
     */
    public UserProfile getProfile() {
        UserProfile userProfile = this.userMapper.getProfile(getUserId());
        return userProfile == null ? UserProfile.defaults(getUserId()) : userProfile;
    }

    /**
     * 更新用户昵称
     *
     * @param nickname 昵称
     */
    public void updateNickname(String nickname) {
        Assert.isTrue(Verify.isNotBlank(nickname), "nickname cannot be blank");
        this.userMapper.updateNickname(getUserId(), nickname);
    }

    /**
     * 更新用户邮箱
     *
     * @param email: 唯一用户邮箱
     */
    public void updateEmail(String email) {
        Assert.isTrue(Verify.verifyEmail(email), "not a email");
        this.userMapper.updateEmail(getUserId(), email);
    }

    /**
     * 更新用户号码
     *
     * @param phone: 唯一用户号码
     */
    public void updatePhone(String phone) {
        Assert.isTrue(Verify.verifyPhoneNumber(phone), "not a phone number");
        this.userMapper.updatePhone(getUserId(), phone);
    }
}
