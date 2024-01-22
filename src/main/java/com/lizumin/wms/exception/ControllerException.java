package com.lizumin.wms.exception;

import com.lizumin.wms.tool.MessageUtil;
import org.springframework.http.HttpStatus;

public class ControllerException extends RuntimeException {
    private ControllerExceptionType type;

    public ControllerException(ControllerExceptionType type) {
        super(MessageUtil.getMessageByDefault(type.getCode()));
        this.type = type;
    }

    public ControllerExceptionType getType() {
        return type;
    }

    public HttpStatus getStatus() {
        return this.type.getStatus();
    }

    public static ControllerException unknown() {
        return new ControllerException(ControllerExceptionType.UNKNOWN);
    }


}
