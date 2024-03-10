package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Zumin Li
 * @date 2024/3/10 23:20
 */
@Mapper
public interface NoticeMapper {
    /**
     * 根据type,获取最新消息
     *
     * @param type
     * @return
     */
    Notice getNoticeByType(String type);
}
