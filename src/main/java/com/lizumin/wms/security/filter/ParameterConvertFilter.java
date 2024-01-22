package com.lizumin.wms.security.filter;


import com.lizumin.wms.exception.AuthenticationUserException;
import com.lizumin.wms.service.UserService;
import com.lizumin.wms.tool.Verify;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ParameterConvertFilter extends OncePerRequestFilter {
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    public static final String SPRING_SECURITY_FORM_PHONE_KEY = "phone";
    public static final String SPRING_SECURITY_FORM_EMAIL_KEY = "email";

    private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
    private String phoneParameter = SPRING_SECURITY_FORM_PHONE_KEY;

    private String emailParameter = SPRING_SECURITY_FORM_EMAIL_KEY;
    private static final AntPathRequestMatcher PHONE_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/phone",
            "POST");

    private static final AntPathRequestMatcher EMAIL_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/email",
            "POST");

    private AntPathRequestMatcher emailMatcher = EMAIL_ANT_PATH_REQUEST_MATCHER;
    private AntPathRequestMatcher phoneMatcher = PHONE_ANT_PATH_REQUEST_MATCHER;

    private UserService userService;

    private boolean postOnly = true;

    public ParameterConvertFilter(UserService userService){
        this.userService = userService;
    }

    /**
     * Retrieval username from email/phone, and wrap it into attribute of request
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }

        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationUserException("Authentication method not supported: " + request.getMethod());
        }

        // 先检查密码字段有没有填，没填直接中断
        if (!Verify.isNotBlank(request.getParameter(this.passwordParameter))) {
            throw new AuthenticationUserException("BPV-000");
        }

        String username = null;

        if (this.phoneMatcher.matches(request)) {
            username = obtainUsernameFromPhone(request);
        } else if (this.emailMatcher.matches(request)){
            username = obtainUsernameFromEmail(request);
        }

        HttpServletRequestWrapper requestWrapper = getHttpServletRequestWrapper(request, username);
        chain.doFilter(requestWrapper, response);
    }

    private static HttpServletRequestWrapper getHttpServletRequestWrapper(HttpServletRequest request, String username) {
        if (username == null) {
            throw new AuthenticationUserException("BPV-004");
        }

        //通过wrapper类改变username时的返回
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                if ("username".equals(name)) {
                    return username;
                }
                return super.getParameter(name);
            }
        };
        return requestWrapper;
    }

    private String obtainUsernameFromEmail(HttpServletRequest request){
        String email = request.getParameter(this.emailParameter);
        if (!Verify.verifyEmail(email)) {
            throw new AuthenticationServiceException("BPV-002");
        }

        return this.userService.getUsernameByEmail(email);
    }

    private String obtainUsernameFromPhone(HttpServletRequest request){
        String phone = request.getParameter(this.phoneParameter);
        if (!Verify.verifyPhoneNumber(phone)) {
            throw new AuthenticationServiceException("BPV-001");
        }

        return this.userService.getUsernameByPhoneNumber(phone);
    }

    private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (EMAIL_ANT_PATH_REQUEST_MATCHER.matches(request) || PHONE_ANT_PATH_REQUEST_MATCHER.matches(request)) {
            return true;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.format("Did not match request to phone/email mather"));
        }
        return false;
    }
}
