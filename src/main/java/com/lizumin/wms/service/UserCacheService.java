package com.lizumin.wms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lizumin.wms.dao.RedisOperator;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.tool.JsonTool;
import com.lizumin.wms.tool.Verify;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;

@Service
public class UserCacheService implements UserCache {
    private final Log logger = LogFactory.getLog(UserCacheService.class);

    private final Duration USER_CACHE_EXPIRE_DURATION = Duration.ofMinutes(30);

    private final String USER_CACHE_PREFIX = "user_cache_";

    private RedisOperator redisOperator;

    @Autowired
    public UserCacheService(RedisOperator redisOperator){
        this.redisOperator = redisOperator;
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        String jsonUser = redisOperator.get(USER_CACHE_PREFIX + username);
        if (!Verify.isNotBlank(jsonUser)) {
            return null;
        }
        User user = null;
        try {
            user = JsonTool.jsonToObj(jsonUser, User.class);
        } catch (JsonProcessingException e){
            logger.trace("Convert json to user failed");
            return null;
        }
        return user;
    }

    @Override
    public void putUserInCache(UserDetails user) {
        Assert.isTrue(user instanceof User, "Only custom user can store in cache");
        User userDetail = (User) user;
        try {
            String json = JsonTool.objToJson(userDetail);
            redisOperator.set(USER_CACHE_PREFIX + user.getUsername(), json, USER_CACHE_EXPIRE_DURATION);
        } catch (JsonProcessingException e){
            logger.trace("Convert json to user failed");
        }
    }

    @Override
    public void removeUserFromCache(String username) {
        redisOperator.remove(USER_CACHE_PREFIX + username);
    }
}
