package com.lizumin.wms.security;

import com.lizumin.wms.entity.User;
import com.lizumin.wms.tool.JwtTokenTool;
import com.lizumin.wms.tool.ResponseTool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;


import java.util.HashMap;
import java.util.Map;

/**
 * Generate JWT when authentication success, and wrap into response
 *
 */
public class JwtGeneratorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Assert.isInstanceOf(User.class, authentication.getPrincipal(), "Authenticated principal must be a UserDetails");
        User user = (User) authentication.getPrincipal();

        String token = JwtTokenTool.generateToken(user.getUsername(), user.getId());
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        ResponseTool.writeJson(response, body);
    }
}
