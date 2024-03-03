package com.lizumin.wms.aop;

import com.lizumin.wms.dao.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Optional;

@Aspect
@Component
public class RequestRateLimitAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String IP_LIMIT_CACHE_PREFIX = "ip_limit_";

    private RedisOperator redisOperator;

    public RequestRateLimitAspect(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @Pointcut("@annotation(requestRateLimit)")
    public void controllerAspect(RequestRateLimit requestRateLimit){}

    @Around(value = "controllerAspect(requestRateLimit)", argNames = "pjp,requestRateLimit")
    public Object doAround(ProceedingJoinPoint pjp, RequestRateLimit requestRateLimit) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();

        if (ra != null) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getResponse();

            HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
            String key = IP_LIMIT_CACHE_PREFIX + request.getRemoteAddr().replace(':', '_');

            // 如果limit为0, 证明第一次访问，或者前面记录的过期了
            int limit = Integer.parseInt(Optional.ofNullable(this.redisOperator.get(key)).orElse("0"));
            if (limit >= requestRateLimit.rate()) {
                logger.error("Exceed IP limit [{} requests/{}ms], IP:{}", requestRateLimit.rate(), requestRateLimit.interval(), request.getRemoteAddr());
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Exceed IP limit");
            }

            limit++;
            // 如果是第一次需要好设置失效时间
            if (limit == 1) {
                this.redisOperator.set(key, String.valueOf(limit), Duration.ofMillis(requestRateLimit.interval()));
            } else {
                this.redisOperator.update(key, String.valueOf(limit));
            }
        }
        return pjp.proceed();
    }
}
