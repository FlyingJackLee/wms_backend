package com.lizumin.wms.entity;

import com.lizumin.wms.tool.MessageUtil;

/**
 * @author Zumin Li
 * @date 2024/2/28 12:35
 */
public class ApiRes {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ApiRes success() {
        return success(MessageUtil.getMessageByContext("SUCCESS"));
    }

    public static ApiRes success(String msg) {
        ApiRes apiRes = new ApiRes();
        apiRes.setMessage(msg);
        return apiRes;
    }

    public static ApiRes fail() {
        return fail(MessageUtil.getMessageByContext("SUCCESS"));
    }

    public static ApiRes fail(String msg) {
        ApiRes apiRes = new ApiRes();
        apiRes.setMessage(msg);
        return apiRes;
    }
}
