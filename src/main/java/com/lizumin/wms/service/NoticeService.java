package com.lizumin.wms.service;

import com.lizumin.wms.dao.NoticeMapper;
import com.lizumin.wms.entity.Notice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author Zumin Li
 * @date 2024/3/10 23:46
 */
@Service
public class NoticeService {
    private NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    /**
     * 根据type获取最新的通告，需要权限
     *
     * @param authentication
     * @param type
     * @return
     */
    public Notice latest(Authentication authentication, String type) {
        Assert.notNull(authentication, "dont have permission");
        type = type == null ? "" : type;

        Notice result = this.noticeMapper.getNoticeByType(type);
        return result == null ? Notice.DefaultObj(type) : result;
    }
}
