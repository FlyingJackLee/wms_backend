package com.lizumin.wms.exception;

import org.springframework.http.HttpStatus;

public enum ControllerExceptionType {
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "BPV-000"),
    PHONE_OR_PASSWORD_NULL(HttpStatus.BAD_REQUEST ,"BPV-001"),
    EMAIL_OR_PASSWORD_NULL(HttpStatus.BAD_REQUEST ,"BPV-002"),

    USERNAME_OR_PASSWORD_NULL(HttpStatus.BAD_REQUEST ,"BPV-003"),

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST ,"BPV-004"),

    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR ,"UNKNOWN");

    private final HttpStatus status;
    private final String code;

    ControllerExceptionType(int status, String code) {
        this.status = HttpStatus.valueOf(status);
        this.code = code;
    }

    ControllerExceptionType(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }
    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
