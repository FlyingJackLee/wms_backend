package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Notice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;

/**
 * Notice Mapper测试
 *
 * @author Zumin Li
 * @date 2024/2/10 21:47
 */
@SpringBootTest
@ActiveProfiles(value = "test")
public class NoticeMapperTest {
    @Autowired
    private NoticeMapper noticeMapper;

    @Test
    public void should_get_latest_relating_notice() {
        Notice update = this.noticeMapper.getNoticeByType("update");
        assertThat(update.getPublishTime().getTime() - 1710343745000L, lessThan(10L));

        Notice warn = this.noticeMapper.getNoticeByType("warn");
        assertThat(warn.getPublishTime().getTime() - 1710170945000L, lessThan(10L));

        Notice empty = this.noticeMapper.getNoticeByType("");
        assertThat(empty, nullValue());

        Notice notExist = this.noticeMapper.getNoticeByType("asd");
        assertThat(notExist, nullValue());
    }
}
