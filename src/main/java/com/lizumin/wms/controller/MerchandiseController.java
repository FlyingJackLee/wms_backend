package com.lizumin.wms.controller;

import com.lizumin.wms.entity.Merchandise;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.service.MerchandiseService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品处理controller
 *
 * @author Zumin Li
 * @date 2024/2/14 19:16
 */
@RestController
@RequestMapping("/merchandise")
public class MerchandiseController {
    private MerchandiseService merchandiseService;

    public MerchandiseController(MerchandiseService merchandiseService) {
        this.merchandiseService = merchandiseService;
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getNonSoldMerchandise(Authentication authentication,
                                                                   @RequestParam @Nullable boolean sold,
                                                                   @RequestParam int limit,
                                                                   @RequestParam int offset) {
        //  如果超过限制，返回400和空list
        if (limit > 999) {
            return ResponseEntity.badRequest().body(Map.of());
        }

        int count  = this.merchandiseService.getMerchandiseCount(authentication, sold);

        limit = limit > count ? count : limit;

        int id = ((User) authentication.getPrincipal()).getId();
        Map<String, Object> data = new HashMap<>(2);
        data.put("count", count);
        data.put("merchandise", merchandiseService.getNonSoldMerchandise(id, limit, offset));

        return ResponseEntity.ok(data);
    }

    @GetMapping("/cate")
    public ResponseEntity<List<Merchandise>> getMerchandiseByCateId(Authentication authentication,
                                                                    @RequestParam("cate_id") int cateId){
        if (cateId < 1) {
            return ResponseEntity.badRequest().body(List.of());
        }

        return ResponseEntity.ok(this.merchandiseService.getMerchandiseByCateId(authentication, cateId));
    }
}
