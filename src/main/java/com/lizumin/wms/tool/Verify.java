package com.lizumin.wms.tool;

import java.util.Date;
import java.util.regex.Pattern;

public class Verify {
    public static final String USERNAME_VERIFY_PATTERN = "^[a-z0-9]{5,15}$";; // 用户名只能是5-15位的小写字母和数字组成

    public static final String PHONE_VERIFY_PATTERN = "^1[3-9]\\d{9}$";
    public static final String EMAIL_VERIFY_PATTERN = "^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    public static final String PASSWORD_VERIFY_PATTER = "^\\S{8,16}$"; // 密码必须为8-16位非空字符

    public static boolean verifyByPattern(String target, String pattern) {
        if ((target != null) && (!target.trim().isEmpty())) {
            return Pattern.matches(pattern, target);
        }
        return false;
    }

    public static boolean isNotBlank(String target) {
        return target != null && !(target.trim().isEmpty());
    }

    public static boolean verifyUsername(String target) {
        return verifyByPattern(target, USERNAME_VERIFY_PATTERN);
    }

    public static boolean verifyPassword(String target) {
        return verifyByPattern(target, PASSWORD_VERIFY_PATTER);
    }

    public static boolean verifyEmail(String target) {
        return verifyByPattern(target, EMAIL_VERIFY_PATTERN);
    }

    public static boolean verifyPhoneNumber(String target) {
        return verifyByPattern(target, PHONE_VERIFY_PATTERN);
    }

    public static boolean verifyTimestamp(long stamp) {
        try {
            Date date = new Date(stamp);
            return stamp == date.getTime();
        } catch (Exception e){
            return false;
        }
    }
}
