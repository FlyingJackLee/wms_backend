package com.lizumin.wms.tool;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTool {
    // 将时间戳转为+8时区的时间
    public static Date zoneEpchDate(long time){
        ZonedDateTime zoneTime = Instant.ofEpochMilli(time).atZone(ZoneId.of("Asia/Shanghai"));

        Date date;
        try {
            date = time == 0L ? new Date() : Date.from(zoneTime.toInstant());
        } catch (Exception e){
            date = new Date();
        }

        return date;
    }
}
