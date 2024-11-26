package com.lizumin.wms.controller;

import com.lizumin.wms.entity.Notice;
import com.lizumin.wms.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zumin Li
 * @date 2024/3/11 0:01
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {
    private NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/")
    public ResponseEntity<Notice> getLatestNotice(Authentication authentication, @RequestParam("type") String type) {
        if (type == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(this.noticeService.latest(authentication, type));
    }
}
