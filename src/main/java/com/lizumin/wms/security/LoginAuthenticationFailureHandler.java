package com.lizumin.wms.security;

import com.lizumin.wms.tool.MessageUtil;
import com.lizumin.wms.tool.ResponseTool;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String message = MessageUtil.getMessageByRequest(exception.getMessage(), request);

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        ResponseTool.writeBody(response, message);
    }
}
