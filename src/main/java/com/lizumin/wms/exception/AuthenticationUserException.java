package com.lizumin.wms.exception;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;


/**
 * Thrown if an authentication request could not be processed due to user.
 *
 */
public class AuthenticationUserException extends AuthenticationException {
    public AuthenticationUserException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>AuthenticationUserException</code> with the specified
     * message and root cause.
     * @param msg the detail message
     * @param cause root cause
     */
    public AuthenticationUserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
