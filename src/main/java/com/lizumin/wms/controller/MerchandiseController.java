package com.lizumin.wms.controller;

import com.lizumin.wms.entity.ApiRes;
import com.lizumin.wms.entity.Merchandise;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.service.MerchandiseService;
import com.lizumin.wms.tool.Verify;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public ResponseEntity<Map<String, Object>> getMerchandise(
                                                              @RequestParam @Nullable boolean sold,
                                                              @RequestParam int limit,
                                                              @RequestParam int offset) {
        //  如果超过限制，返回400和空list
        if (limit > 999 || limit <=0 || offset < 0) {
            return ResponseEntity.badRequest().body(Map.of());
        }

        int count  = this.merchandiseService.getMerchandiseCount(sold);
        limit = limit > count ? count : limit;


        Map<String, Object> data = new HashMap<>(2);
        data.put("count", count);
        data.put("merchandise", merchandiseService.getMerchandiseByPage(limit, offset));

        return ResponseEntity.ok(data);
    }

    @GetMapping("/cate")
    public ResponseEntity<List<Merchandise>> getMerchandiseByCateId(@RequestParam("cate_id") int cateId){
        if (cateId < 1) {
            return ResponseEntity.badRequest().body(List.of());
        }

        return ResponseEntity.ok(this.merchandiseService.getMerchandiseByCateId(cateId));
    }

    @PostMapping("/")
    public ResponseEntity<ApiRes> insertMerchandise(@RequestParam("cate_id") int cateId,
                                                    @RequestParam("cost") double cost,
                                                    @RequestParam("price") double price,
                                                    @RequestParam("imei_list") List<String> imeiList,
                                                    @RequestParam("create_time") long createTime
    ) {
        if (cateId < 1) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }
        Date date = new Date(createTime);

        this.merchandiseService.insertMerchandise(cateId, cost, price, imeiList, date);
        return ResponseEntity.ok(ApiRes.success());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiRes> updateMerchandise(@PathVariable int id,
                                                    @RequestParam("cost") double cost,
                                                    @RequestParam("price") double price,
                                                    @RequestParam("imei") String imei){
        if (id < 1 || !Verify.isNotBlank(imei)) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        this.merchandiseService.updateMerchandise(id, cost, price, imei);
        return ResponseEntity.ok(ApiRes.success());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes> deleteMerchandise( @PathVariable int id){
        if (id < 1) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }
        this.merchandiseService.deleteMerchandise(id);
        return ResponseEntity.ok(ApiRes.success());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Merchandise>> search( @RequestParam("text") String text, @RequestParam @Nullable boolean sold) {
        if (!Verify.isNotBlank(text)) {
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok(this.merchandiseService.searchMerchandise(text, sold));
    }
}
