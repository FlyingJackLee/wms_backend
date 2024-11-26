package com.lizumin.wms.entity;

import java.util.Date;

/**
 * @author Zumin Li
 * @date 2024/3/10 23:15
 */
public class Notice {
    private int id;

    private String type;

    private Date publishTime;

    private String content; // 支持html显

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public static Notice DefaultObj(String type) {
        Notice notice = new Notice();
        notice.id = 0;
        notice.type = type;
        notice.content = "无";
        notice.publishTime = new Date();

        return notice;
    }
}
