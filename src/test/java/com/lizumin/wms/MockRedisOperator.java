package com.lizumin.wms;

import com.lizumin.wms.dao.RedisOperator;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class MockRedisOperator implements RedisOperator {
    private final static Duration DEFAULT_EXPIRED_TIME = Duration.ofHours(24);

    private ConcurrentHashMap<String, String>  cache = new ConcurrentHashMap<>(1);
    private Timer timer = new Timer();

    @Override
    public void set(String value, Duration expireHour) {
        cache.put(value, "1");
        timer.schedule(new RemoveTask(value, cache), expireHour.toMillis());
    }

    @Override
    public void set(String value) {
        cache.put(value, "1");
        timer.schedule(new RemoveTask(value, cache), DEFAULT_EXPIRED_TIME.toMillis());
    }

    @Override
    public void set(String key, String value, Duration expireHour) {
        cache.put(key, value);
        timer.schedule(new RemoveTask(key, cache), expireHour.toMillis());
    }

    @Override
    public void set(String key, String value) {
        cache.put(key, value);
        timer.schedule(new RemoveTask(key, cache), DEFAULT_EXPIRED_TIME.toMillis());
    }

    @Override
    public void update(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void setExpire(String key, Duration expireHour) {
        timer.schedule(new RemoveTask(key, cache), expireHour.toMillis());
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public boolean hasKey(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    @Override
    public Date getExpire(String key) {
        return null;
    }

    public Map<String, String> getCache() {
        return cache;
    }

    public void setCache(ConcurrentHashMap<String, String> cache) {
        this.cache = cache;
    }

    private static class RemoveTask extends TimerTask {
        private String key;
        private ConcurrentHashMap<String, String> cache;

        public RemoveTask(String key, ConcurrentHashMap<String, String> cache) {
            this.key = key;
            this.cache = cache;
        }

        @Override
        public void run() {
            if (cache.contains(key)) {
                cache.remove(key);
            }
        }
    }
}
