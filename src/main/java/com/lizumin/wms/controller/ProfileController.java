package com.lizumin.wms.controller;

import com.lizumin.wms.entity.ApiRes;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.UserProfile;
import com.lizumin.wms.service.AuthorityService;
import com.lizumin.wms.service.ProfileService;
import com.lizumin.wms.tool.Verify;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/16 20:05
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {
    private ProfileService profileService;
    private AuthorityService authorityService;

    public ProfileController(ProfileService profileService, AuthorityService authorityService) {
        this.profileService = profileService;
        this.authorityService = authorityService;
    }

    /**
     * GET /profile/
     * 获取当前用户档案
     */
    @GetMapping("/")
    public ResponseEntity<UserProfile> getProfile() {
        return ResponseEntity.ok(this.profileService.getProfile());
    }

    /**
     * 更新nickname
     *
     * @param nickname: 昵称
     */
    @PutMapping("/nickname")
    public ResponseEntity<ApiRes> updateNickname(@RequestParam("nickname") String nickname) {
        if (!Verify.isNotBlank(nickname)) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        this.profileService.updateNickname(nickname);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 获取当前用户角色
     */
    @GetMapping("/role")
    public ResponseEntity<SystemAuthority> getRole() {
        return ResponseEntity.ok(this.authorityService.getRole());
    }

    /**
     * 获取当前用户权限
     */
    @GetMapping("/permission")
    public ResponseEntity<List<SystemAuthority>> getPermissions() {
        return ResponseEntity.ok(this.authorityService.getPermission());
    }
}
