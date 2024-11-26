package com.lizumin.wms.controller;

import com.lizumin.wms.entity.ApiRes;
import com.lizumin.wms.entity.Order;
import com.lizumin.wms.service.OrderService;
import com.lizumin.wms.tool.DateTool;
import com.lizumin.wms.tool.Verify;

import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;

/**
 * @author Zumin Li
 * @date 2024/3/1 16:40
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiRes> createOrder(@RequestParam("me_id") int meId,
                                              @RequestParam("selling_price") double sellingPrice,
                                              @RequestParam("selling_time") @Nullable long sellingTime,
                                              @RequestParam String remark) {
        if (meId < 1){
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        Date date = DateTool.zoneEpchDate(sellingTime);
        int id = this.orderService.insertOrder(meId, sellingPrice, remark, date);
        return ResponseEntity.ok(ApiRes.success(String.valueOf(id)));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiRes> batchCreateOrder(@RequestBody List<Order> orders) {
        if (orders == null || orders.isEmpty()){
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        this.orderService.insertOrder(orders);
        return ResponseEntity.ok(ApiRes.success());
    }

    @GetMapping("/range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(@Param("start") long start, @Param("end") long end) {
        if (!Verify.verifyTimestamp(start) || !Verify.verifyTimestamp(end) ){
            return ResponseEntity.badRequest().body(List.of());
        }

        Date sellingTimeStart = DateTool.zoneEpchDate(start);
        Date sellingTimeEnd = DateTool.zoneEpchDate(end);

        return ResponseEntity.ok(this.orderService.getOrdersByDateRange(sellingTimeStart, sellingTimeEnd));
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<ApiRes> returningOrder(@PathVariable("id") int orderId) {
        if (orderId <= 0) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        this.orderService.returnOrder(orderId);
        return ResponseEntity.ok(ApiRes.success());
    }
}

