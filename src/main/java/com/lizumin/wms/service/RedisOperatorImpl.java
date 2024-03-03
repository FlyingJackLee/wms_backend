package com.lizumin.wms.service;

import com.lizumin.wms.dao.RedisOperator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisOperatorImpl implements RedisOperator {
    // 默认过期时间（小时）
    private final static Duration DEFAULT_EXPIRED_TIME = Duration.ofHours(24);
    private final static String DEFAULT_VALUE = "1";

    private final RedisTemplate<String,String> redisTemplate;

    public RedisOperatorImpl(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Duration expireHour) {
        this.redisTemplate.boundValueOps(key).set(DEFAULT_VALUE, expireHour);
    }

    @Override
    public void set(String key) {
        this.redisTemplate.boundValueOps(key).set(DEFAULT_VALUE, DEFAULT_EXPIRED_TIME);
    }

    @Override
    public void set(String key, String value, Duration expiredHour) {
        redisTemplate.boundValueOps(key).set(value, expiredHour);
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.boundValueOps(key).set(value, DEFAULT_EXPIRED_TIME);
    }

    @Override
    public void update(String key, String value) {
        int ttl = redisTemplate.opsForValue().getOperations().getExpire(key).intValue();
        redisTemplate.boundValueOps(key).set(value, ttl, TimeUnit.SECONDS);
    }

    @Override
    public void setExpire(String key, Duration expireHour) {
        redisTemplate.expire(key, expireHour);
    }

    @Override
    public String get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Date getExpire(String key) {
        Long date = redisTemplate.getExpire(key);
        return date == null ? null : new Date(date);
    }
}
