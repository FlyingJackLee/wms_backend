package com.lizumin.wms.controller;

import com.lizumin.wms.entity.ApiRes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zumin Li
 * @date 2024/3/14 0:57
 */
@RestController
@RequestMapping("/group")
public class GroupController {
    @GetMapping("/")
    @PreAuthorize("hasAuthority('PERMISSION:shopping')")
    public ResponseEntity<ApiRes> test(){
        return ResponseEntity.ok(ApiRes.success());
    }
}
