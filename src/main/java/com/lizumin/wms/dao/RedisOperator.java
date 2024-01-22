package com.lizumin.wms.dao;

import java.time.Duration;
import java.util.Date;

public interface RedisOperator {
    /**
     * 根据过期时间储存值
     *
     * @param value
     * @param expireHour
     */
    void set(String value, Duration expireHour);

    /**
     * 默认时间储存值
     *
     * @param value
     */
    void set(String value);

    /**
     * 根据过期时间储存键值
     *
     * @param key
     * @param value
     */
    void set(String key, String value, Duration expireHour);

    /**
     * 默认过期时间储存值
     *
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * 更新值
     *
     * @param key
     * @param value
     */
    void update(String key, String value);

    /**
     * 设置过期时间
     *
     * @param key
     */
    void setExpire(String key, Duration expireHour);

    /**
     * 获取key值
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * key是否存在
     *
     * @param key
     * @return
     */
    boolean hasKey(String key);

    /**
     * 删除值
     *
     * @param key
     */
    void remove(String key);

    /**
     * 返回过期时间
     *
     * @param key
     * @return
     */
    Date getExpire(String key);
}
