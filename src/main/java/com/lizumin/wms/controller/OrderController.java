package com.lizumin.wms.controller;

import com.lizumin.wms.entity.ApiRes;
import com.lizumin.wms.entity.Merchandise;
import com.lizumin.wms.entity.Order;
import com.lizumin.wms.service.OrderService;
import com.lizumin.wms.tool.Verify;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

        Date date = sellingTime == 0L ? new Date() : new Date(sellingTime);
        int id = this.orderService.insertOrder(meId, sellingPrice, remark, date);
        return ResponseEntity.ok(ApiRes.success(String.valueOf(id)));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiRes> batchCreateOrder(Authentication authentication, @RequestBody List<Order> orders) {
        if (orders == null || orders.isEmpty()){
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        this.orderService.insertOrder(orders);
        return ResponseEntity.ok(ApiRes.success());
    }

    @GetMapping("/range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(Authentication authentication, @Param("start") long start, @Param("end") long end) {
        if (!Verify.verifyTimestamp(start) || !Verify.verifyTimestamp(end) ){
            return ResponseEntity.badRequest().body(List.of());
        }

        Date sellingTimeStart = new Date(start);
        Date sellingTimeEnd = new Date(end);
        return ResponseEntity.ok(this.orderService.getOrdersByDateRange(sellingTimeStart, sellingTimeEnd));
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<ApiRes> returningOrder(Authentication authentication, @PathVariable("id") int orderId) {
        if (orderId <= 0) {
            return ResponseEntity.badRequest().body(ApiRes.fail());
        }

        this.orderService.returnOrder(orderId);
        return ResponseEntity.ok(ApiRes.success());
    }
}

