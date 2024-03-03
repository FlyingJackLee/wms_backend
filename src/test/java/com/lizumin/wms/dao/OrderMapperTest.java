package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Order;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
/**
 * @author Zumin Li
 * @date 2024/3/1 14:58
 */
@SpringBootTest
@ActiveProfiles(value = "test")
public class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MerchandiseMapper merchandiseMapper;

    /**
     * 参数有误时insertOrder测试
     */
    @Test
    public void should_throw_exception_when_illegal_paras() {
        int meId =  merchandiseMapper.insertMerchandise(11, 999, 12989, "ime1", new Date(), 1);

        // me_id 有误
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->{
            this.orderMapper.insertOrder(999, 999.0, "备注", new Date(), 1);
        });

        // own id 有误
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->{
            this.orderMapper.insertOrder(meId, 999.0, "备注", new Date(),99);
        });

        // 重复销售
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->{
            this.orderMapper.insertOrder(meId, 999.0, "备注", new Date(),1);
            this.orderMapper.insertOrder(meId, 999.0, "备注", new Date(),1);
        });
    }

    /**
     * insertOrder测试
     */
    @Test
    public void should_insert_order() {
        Date date = generateDate(2005,1, 1);
        int meId =  merchandiseMapper.insertMerchandise(11, 999, 12989, "ime2", new Date(), 1);
        int id = this.orderMapper.insertOrder(meId, 999.0, "备注", date ,1);
        assertThat(id, greaterThan(0));
        assertThat(orderMapper.getOrderById(id, 1).getSellingTime(), equalTo(date));
    }

    /**
     * getOrdersByDateRange测试
     */
    @Test
    public void should_get_relevant_list_when_get_orders() {
        // 测试数据准备
        Date date1 = generateDate(2000,1,1);
        int meId =  merchandiseMapper.insertMerchandise(11, 999, 1299, "imeget1", date1, 1);
        this.orderMapper.insertOrder(meId, 999.0, "备注1", date1 ,1);

        Date date2 = generateDate(2001,1,1);
        meId =  merchandiseMapper.insertMerchandise(11, 999, 1299, "imeget2", date2, 1);
        this.orderMapper.insertOrder(meId, 999.0, "备注2", date2 ,1);

        Date date3 = generateDate(2001,1,2);
        meId =  merchandiseMapper.insertMerchandise(11, 799, 1099, "imeget3", date3, 1);
        this.orderMapper.insertOrder(meId, 999.0, "备注3", date3 ,1);

        List<Order> orders = this.orderMapper.getOrdersByDateRange(1, date1, date2);
        assertThat(orders.size(), is(2));
        assertThat(orders.getFirst().getMerchandise(), notNullValue());
        assertThat(orders.getFirst().getRemark(), notNullValue());
        assertThat(orders.getFirst().getSellingTime(), notNullValue());
        assertThat(orders.getFirst().getId(), greaterThan(0));
        assertThat(orders.getFirst().getMerchandise().getCategory(), notNullValue());

        List<Order> emptyOrders = this.orderMapper.getOrdersByDateRange(1, generateDate(1900, 1, 1), generateDate(1901, 1, 1));
        assertThat(emptyOrders, empty());
    }

    /**
     * setOrderReturned测试
     */
    @Test
    public void should_update_returned() {
        int meId =  merchandiseMapper.insertMerchandise(11, 999, 1299, "imeiupdate", new Date(), 1);
        int id = this.orderMapper.insertOrder(meId, 999.0, "备注1", new Date() ,1);
        this.orderMapper.setOrderReturned(id, true, 1);
        assertThat(this.orderMapper.getOrderById(id, 1).isReturned(), is(true));
        this.orderMapper.setOrderReturned(id, false, 1);
        assertThat(this.orderMapper.getOrderById(id, 1).isReturned(), is(false));
    }

    private Date generateDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        return calendar.getTime();
    }
}
