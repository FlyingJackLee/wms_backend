package com.lizumin.wms.controller;

import com.lizumin.wms.entity.ApiRes;
import com.lizumin.wms.entity.Group;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.UserProfile;
import com.lizumin.wms.service.AuthorityService;
import com.lizumin.wms.service.GroupService;
import com.lizumin.wms.service.UserService;
import com.lizumin.wms.tool.Verify;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/14 0:57
 */
@RestController
@RequestMapping("/group")
public class GroupController {
    private GroupService groupService;
    private UserService userService;

    private AuthorityService authorityService;

    public GroupController(GroupService groupService, UserService userService, AuthorityService authorityService) {
        this.groupService = groupService;
        this.userService = userService;
        this.authorityService = authorityService;
    }

    /**
     * 返回当前用户的所属组
     */
    @GetMapping("/")
    public ResponseEntity<Group> getGroup(){
        Group group = this.groupService.getGroupOfCurrentUser();
        return ResponseEntity.ok(group);
    }

    /**
     * 创建新的group
     *
     * @param storeName group名
     * @param address 地址，可以为空
     * @param contact 联系电话： 可以为空
     * @param createTime 创建时间：默认为时间
     */
    @PostMapping("/")
    public ResponseEntity<ApiRes> createGroup(@RequestParam("storeName") @NonNull String storeName,
                                              @RequestParam("address") @Nullable String address,
                                              @RequestParam("contact") @Nullable String contact,
                                              @RequestParam("createTime") @Nullable long createTime){
        Date date = createTime == 0L ? new Date() : new Date(createTime);
        this.groupService.createGroup(storeName, address, contact, date);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 更新店名
     */
    @PutMapping("/storename")
    public ResponseEntity<ApiRes> updateStoreName(@RequestParam("storeName") @NonNull String storeName){
        this.groupService.updateStoreName(storeName);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 更新店地址
     */
    @PutMapping("/address")
    public ResponseEntity<ApiRes> updateAddress(@RequestParam("address") @NonNull String address){
        this.groupService.updateAddress(address);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 更新店联系方式
     */
    @PutMapping("/contact")
    public ResponseEntity<ApiRes> updateContact(@RequestParam("contact") @NonNull String contact){
        this.groupService.updateContact(contact);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 获取当前group下的用户
     */
    @GetMapping("/staffs")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<UserProfile>> getUsersInGroup(){
        return ResponseEntity.ok(this.groupService.getUsersInGroup());
    }

    /**
     * 删除group下的用户
     */
    @DeleteMapping("/staff")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiRes> deleteUserInGroup(@RequestParam("userId") int userId) {
        if (userId <= 0) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }
        this.groupService.deleteUserInGroup(userId);

        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 根据id新建加入请求
     */
    @PostMapping("/join/id")
    @PreAuthorize("hasRole('DEFAULT')")
    public ResponseEntity<ApiRes> createRequestByGroupId(@RequestParam("groupId") int groupId ){
        if (groupId <= 0) {
            return ResponseEntity.badRequest().body(ApiRes.fail("目标组不存在"));
        }

        try {
            this.groupService.createJoinRequest(groupId);
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(ApiRes.fail("组不存在或重复请求"));
        }

        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 根据拥有者手机号查找group并新增加入请求
     */
    @PostMapping("/join/phone")
    @PreAuthorize("hasRole('DEFAULT')")
    public ResponseEntity<ApiRes> createRequestByOwnerPhone(@RequestParam("phone") String ownerPhone){
        if (!Verify.verifyPhoneNumber(ownerPhone)) {
            return ResponseEntity.badRequest().body(ApiRes.fail("手机不正确"));
        }
        int groupId = this.userService.getGroupIdByPhone(ownerPhone);
        if (groupId <= 0) {
            return ResponseEntity.badRequest().body(ApiRes.fail("未查询到组"));
        }

        try {
            this.groupService.createJoinRequest(groupId);
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(ApiRes.fail("重复请求"));
        }

        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 获取当前用户的group加入请求
     */
    @GetMapping("/join/")
    @PreAuthorize("hasRole('DEFAULT')")
    public ResponseEntity<Group> getGroupInRequestCurrentUser() {
        return ResponseEntity.ok(this.groupService.getGroupInRequestCurrentUser());
    }

    /**
     * 删除当前用户加入请求
     */
    @DeleteMapping("/join/delete")
    @PreAuthorize("hasRole('DEFAULT')")
    public ResponseEntity<ApiRes> deleteRequestCurrentUser() {
        this.groupService.deleteRequest();
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 删除特定用户加入请求
     */
    @DeleteMapping("/join/delete/id")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiRes> deleteRequest(@RequestParam("userId") int userId) {
        this.groupService.deleteRequest(userId);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 获取当前group下所有申请
     */
    @GetMapping("/join/users")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<UserProfile>> getUsersUnderRequest() {
        return ResponseEntity.ok(this.groupService.getUsersUnderRequest());
    }

    /**
     * 同意加入申请并设置权限
     */
    @PostMapping("/join/agree")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiRes> agreeJoinRequest(@RequestParam("userId") int userId,
                                                   @RequestParam("shopping") boolean shopping,
                                                   @RequestParam("inventory") boolean inventory,
                                                   @RequestParam("statistics") boolean statistics) {

        List<SystemAuthority> validPermissions = SystemAuthority.permissions(shopping, inventory, statistics);
        this.groupService.approveJoinGroupRequest(userId, validPermissions);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 更新模块权限
     *
     * @param userId 用户id
     * @param shopping 收银模块权限
     * @param inventory 库存管理权限
     * @param statistics 订单查询权限
     */
    @PutMapping("/permissions")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiRes> updatePermissions(@RequestParam("userId") int userId,
                                                    @RequestParam("shopping") boolean shopping,
                                                    @RequestParam("inventory") boolean inventory,
                                                    @RequestParam("statistics") boolean statistics) {
        if (userId <= 0) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        this.authorityService.updatePermission(userId, shopping, inventory, statistics);
        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 获取用户权限
     *
     * @param userId 用户id
     */
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<SystemAuthority>> getPermissionByUserId(@RequestParam("userId") int userId) {
        if (userId <= 0) {
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok(this.authorityService.getPermissionsByUserId(userId));
    }
}
