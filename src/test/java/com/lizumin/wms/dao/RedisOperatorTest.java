package com.lizumin.wms.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles(value = "test")
public class RedisOperatorTest {
    @Autowired
    private RedisOperator redisOperator;

    /**
     * set测试 - 正常设置
     *
     */
    @Test
    public void should_set_value_when_key_is_right() {
        redisOperator.set("key_1", Duration.ofHours(1));
        assertThat(redisOperator.hasKey("key_1"), is(true));
        assertThat(redisOperator.getExpire("key_1"), notNullValue());

        redisOperator.set("key_2");
        assertThat(redisOperator.hasKey("key_2"), is(true));
        assertThat(redisOperator.getExpire("key_2"), notNullValue());

        redisOperator.set("key_3", "value_3", Duration.ofHours(1));
        assertThat(redisOperator.hasKey("key_3"), is(true));
        assertThat(redisOperator.get("key_3"), equalTo("value_3"));
        long diff = Duration.ofHours(1).toSeconds() - redisOperator.getExpire("key_3").getTime();
        assertThat(diff < 1000, is(true));

        redisOperator.set("key_4", "value_4");
        assertThat(redisOperator.hasKey("key_4"), is(true));
        long previousTime = redisOperator.getExpire("key_4").getTime();;
        diff = Duration.ofHours(24).toSeconds() - previousTime;
        assertThat(diff < 1000, is(true));

        redisOperator.update("key_4", "value_modified");
        long afterTime = redisOperator.getExpire("key_4").getTime();;
        assertThat(afterTime - previousTime < 10, equalTo(true));
        assertThat(redisOperator.get("key_4"), equalTo("value_modified"));

        redisOperator.remove("key_1");
        redisOperator.remove("key_2");
        redisOperator.remove("key_3");
        redisOperator.remove("key_4");
    }

    /**
     * setExpire测试
     *
     */
    @Test
    public void should_set_expire_when_key_exist() {
        redisOperator.set("key_5", "value_5", Duration.ofHours(1));
        redisOperator.setExpire("key_5", Duration.ofSeconds(30));
        long diff = Duration.ofSeconds(30).toSeconds() - redisOperator.getExpire("key_5").getTime();
        assertThat(diff < 1000, is(true));

        redisOperator.remove("key_5");
    }

    /**
     * hasKey remove测试
     *
     */
    @Test
    public void should_remove_key_when_key_exist() {
        assertThat(redisOperator.hasKey("key_6"), is(false));

        redisOperator.set("key_6", "value_5", Duration.ofHours(1));
        assertThat(redisOperator.hasKey("key_6"), is(true));

        redisOperator.remove("key_6");
        assertThat(redisOperator.hasKey("key_6"), is(false));
    }
}
