package com.lizumin.wms.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends AuthenticationException {
    public UserNotFoundException(String msg){ super(msg);}

    public UserNotFoundException(String msg, Throwable tx) { super(msg, tx);}
}
