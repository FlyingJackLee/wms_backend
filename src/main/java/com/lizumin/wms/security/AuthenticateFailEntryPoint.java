package com.lizumin.wms.security;

import com.lizumin.wms.exception.AuthenticationUserException;
import com.lizumin.wms.tool.MessageUtil;
import com.lizumin.wms.tool.ResponseTool;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;

public class AuthenticateFailEntryPoint implements AuthenticationEntryPoint {
    private static final Log logger = LogFactory.getLog(AuthenticateFailEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.debug("Authentication failed.");
        String message = MessageUtil.getMessageByRequest(authException.getMessage(), request);
        if (authException instanceof AuthenticationUserException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        ResponseTool.writeValue(response, "error", message);
    }
}
