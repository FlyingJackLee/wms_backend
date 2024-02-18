package com.lizumin.wms.security;

import com.lizumin.wms.security.filter.JwtAuthenticationFilter;
import com.lizumin.wms.security.filter.ParameterConvertFilter;
import com.lizumin.wms.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String[] WHITE_LIST = {"login/*", "/error", "/user/**", "/signup/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ParameterConvertFilter parameterConvertFilter,
                                                   UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests.requestMatchers(WHITE_LIST).permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(custom -> custom.authenticationEntryPoint(new AuthenticateFailEntryPoint()))
                .addFilterAfter(parameterConvertFilter, ExceptionTranslationFilter.class)
                .addFilterAfter(usernamePasswordAuthenticationFilter, ParameterConvertFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, ParameterConvertFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 用于将非username参数转换为username
     *
     * @param userService
     * @return
     */
    @Bean ParameterConvertFilter parameterConvertFilter(UserService userService) {
        return new ParameterConvertFilter(userService);
    }

    /**
     * username password登录用filter，通过SuccessHandler包装jwt
     *
     * @param authenticationManager
     * @return
     */
    @Bean
    public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAllowSessionCreation(false);
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login/*", "POST"));
        filter.setAuthenticationSuccessHandler(new JwtGeneratorAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(new LoginAuthenticationFailureHandler());
        return filter;
    }

    /**
     * 用于UsernamePasswordAuthenticationFilter生成jwt的handler
     *
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler tokenAuthenticationSuccessHandler() {
        return new JwtGeneratorAuthenticationSuccessHandler();
    }

    /**
     * 用于UsernamePasswordAuthenticationFilter验证用的manager
     * 与数据库的交互逻辑在这里
     *
     * @param userDetailsService
     * @param passwordEncoder
     * @param userCache
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UserCache userCache) {
        UserAuthenticationProvider authenticationProvider = new UserAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserCache(userCache);

        return new ProviderManager(authenticationProvider);
    }

    /**
     * 密码编码器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT的验证用filter
     *
     * @param userService
     * @return
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserService userService) {
        return new JwtAuthenticationFilter(userService);
    }
}
