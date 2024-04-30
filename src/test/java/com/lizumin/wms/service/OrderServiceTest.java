package com.lizumin.wms.service;

import com.lizumin.wms.dao.OrderMapper;
import com.lizumin.wms.entity.Merchandise;
import com.lizumin.wms.entity.Order;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * @author Zumin Li
 * @date 2024/3/1 16:34
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class OrderServiceTest {
    @MockBean
    private MerchandiseService merchandiseService;
    @MockBean
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    /**
     * insertOrder测试 - DEFAULT
     *
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_insert_order_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class,() ->
                this.orderService.insertOrder( 0, 999, "text", new Date()));

        List<Order> orders = new ArrayList<>(1);
        Assertions.assertThrows(AccessDeniedException.class,() -> this.orderService.insertOrder(orders));
    }

    /**
     * insertOrder测试
     *
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_call_both_services_when_insert_order() {
        Assertions.assertThrows(IllegalArgumentException.class,() ->
                this.orderService.insertOrder( 0, 999, "text", new Date()));

        // 对商品无权测试 - checkMePermission返回false
        Mockito.when(this.merchandiseService.getMerchandiseById(2)).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class,() ->
                this.orderService.insertOrder( 2, 999,"text", new Date()));


        // 对商品有权测试 - checkMePermission返回true
        Date date = new Date();
        Mockito.when(this.merchandiseService.getMerchandiseById(2)).thenReturn(Mockito.mock(Merchandise.class));
        this.orderService.insertOrder(2, 999,"text", date);
        verify(orderMapper, times(1)).insertOrder(2, 999, "text", date, 1, 2);
        verify(merchandiseService, times(1)).updateSold(2, true);
    }

    /**
     * insertOrder批量插入测试
     *
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_loop_insert_when_insert() {
        Mockito.when(this.merchandiseService.getMerchandiseById( 3)).thenReturn(Mockito.mock(Merchandise.class));
        Mockito.when(this.merchandiseService.getMerchandiseById(4)).thenReturn(Mockito.mock(Merchandise.class));

        Date date = new Date();
        List<Order> orders = new ArrayList<>(1);

        Order order1 = new Order();
        Merchandise m1 = new Merchandise();
        m1.setId(3);
        order1.setMerchandise(m1);
        order1.setSellingPrice(1999.0);
        order1.setSellingTime(date);
        order1.setRemark("sell");
        orders.add(order1);
        Order order2 = new Order();
        Merchandise m2 = new Merchandise();
        m2.setId(4);
        order2.setMerchandise(m2);
        order2.setSellingPrice(1900.0);
        order2.setSellingTime(date);
        order2.setRemark("sell");
        orders.add(order2);

        this.orderService.insertOrder(orders);
        verify(orderMapper, times(1)).insertOrder(3, 1999.0, "sell", date, 1, 2);
        verify(orderMapper, times(1)).insertOrder(4, 1900.0, "sell", date, 1, 2);
        verify(merchandiseService, times(1)).updateSold(3, true);
        verify(merchandiseService, times(1)).updateSold(4, true);
    }

    /**
     * getOrdersByDateRange测试 - DEFAULT
     *
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_orders_with_bad_user_role() {
        Date d1 = generateDate(2000,1,1);
        Date d2 = generateDate(2001,1,1);

        Assertions.assertThrows(AccessDeniedException.class,() ->
                this.orderService.getOrdersByDateRange(d1, d2)
        );
    }

    /**
     * getOrdersByDateRange测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_throws_or_call_mapper_with_date_range() {
        Date d1 = generateDate(2000,1,1);
        Date d2 = generateDate(2001,1,1);
        Date d3 = generateDate(2001,1,6);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.orderService.getOrdersByDateRange( d1, d3);
        });

        this.orderService.getOrdersByDateRange(d1, d2);
        verify(orderMapper, times(1)).getOrdersByDateRange(2 , d1, d2);
    }

    /**
     * returnOrder测试 - DEFAULT
     *
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_returned_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class,() ->
                this.orderService.returnOrder( 0)
        );
    }

    /**
     * 退货测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_throws_or_call_mapper_when_returning_order() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.orderService.returnOrder( 0);
        });

        Merchandise meMock = Mockito.mock(Merchandise.class);
        when(meMock.getId()).thenReturn(99);
        Order orderMock = Mockito.mock(Order.class);
        when(orderMock.getMerchandise()).thenReturn(meMock);
        when(this.orderMapper.getOrderById(2, 2)).thenReturn(orderMock);
        this.orderService.returnOrder(2);
        verify(merchandiseService, times(1)).updateSold(99, false);
        verify(orderMapper, times(1)).setOrderReturned(2 ,true, 2);

    }

    private Date generateDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        return calendar.getTime();
    }
}
