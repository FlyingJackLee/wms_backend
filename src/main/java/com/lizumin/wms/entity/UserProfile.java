package com.lizumin.wms.entity;

public class UserProfile {
    private int userId;

    private String nickname;

    private String email;
    private String phoneNumber;
    private String avatar;

    public int getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 返回一个替代null状态下的默认对象
     *
     * @param userId
     * @return
     */
    public static UserProfile defaults(int userId){
        UserProfile result = new UserProfile();
        result.setUserId(userId);
        result.setNickname("");
        result.setAvatar("default");
        result.setEmail("");
        result.setPhoneNumber("");
        return result;
    }
}
