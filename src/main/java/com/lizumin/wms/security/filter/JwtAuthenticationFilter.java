package com.lizumin.wms.security.filter;

import com.lizumin.wms.entity.User;
import com.lizumin.wms.exception.AuthenticationUserException;
import com.lizumin.wms.service.UserService;
import com.lizumin.wms.tool.JwtTokenTool;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    private UserService userService;

    public JwtAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = checkHeader(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // verify token
        Optional<Jws<Claims>> claims = JwtTokenTool.verifyAndGetClaims(token);
        if (claims.isEmpty()) {
            throw new AuthenticationUserException("BPV-005");
        }
        Optional<String> username = claims.get().getPayload().getAudience().stream().findFirst();
        if (username.isEmpty()) {
            throw new AuthenticationUserException("BPV-006");
        }

        User user =  this.userService.lazyLoadUserByUsername(username.get());
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, null, user.getAuthorities());

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authentication));
        }

        filterChain.doFilter(request, response);
    }

    private String checkHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            return null;
        }

        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BEARER)) {
            return null;
        }

        if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
            return null;
        }

        return header.substring(7);
    }
}
