package com.lizumin.wms.dao;

import com.lizumin.wms.entity.MeCount;
import com.lizumin.wms.entity.Merchandise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * MerchandiseMapper测试
 *
 * @author Zumin Li
 * @date 2024/2/14 0:45
 */
@SpringBootTest
@ActiveProfiles(value = "test")
public class MerchandiseMapperTest {
    @Autowired
    private MerchandiseMapper merchandiseMapper;

    /**
     * 数量查询测试
     */
    @Test
    public void should_get_count_when_get_count_by_id() {
        int count = this.merchandiseMapper.getMerchandiseCount(1, false);
        assertThat(count, greaterThan(2));

        count = this.merchandiseMapper.getMerchandiseCount(1, true);
        assertThat(count, greaterThanOrEqualTo(1));

        count = this.merchandiseMapper.getMerchandiseCount(9999, false);
        assertThat(count, is(0));
    }

    /**
     * 获取商品正常测试
     */
    @Test
    public void should_get_right_entity_when_input_a_valid_id() {
        Merchandise me = this.merchandiseMapper.getMerchandiseById(1, 1);
        assertThat(me.getCategory().getId(), is(11));
        assertThat(me.getCost(), closeTo(1750.00, 2));
        assertThat(me.getPrice(), closeTo(1999.00, 2));
        assertThat(me.getImei(), equalTo("123456789012340"));
        assertThat(me.getCreateTime(), notNullValue());
    }

    /**
     * 不存在商品查找测试
     */
    @Test
    public void should_get_null_when_input_a_invalid_id() {
        Merchandise me = this.merchandiseMapper.getMerchandiseById(9999, 1);
        assertThat(me, nullValue());
    }

    /**
     * 获取用户所有商品测试
     */
    @Test
    public void should_get_relevant_merchandise_when_giving_owner_id() {
        List<Merchandise> merchandises = this.merchandiseMapper.getAllMerchandise(1, false, 3, 0);
        assertThat(merchandises.size(), is(3));
        assertThat(merchandises.get(0).getId(), is(1));

        merchandises = this.merchandiseMapper.getAllMerchandise(999, false ,3, 0);
        assertThat(merchandises, empty());
        merchandises = this.merchandiseMapper.getAllMerchandise(1, false ,99, 99);
        assertThat(merchandises, empty());
    }

    /**
     * 获取用户所有已售商品测试
     */
    @Test
    public void should_get_sold_me_when_giving_id() {
        int sold = this.merchandiseMapper.getAllMerchandise(1,true , 999, 0).size();
        int nonSold = this.merchandiseMapper.getAllMerchandise(1,false , 999, 0).size();
        assertThat(sold + nonSold, greaterThan(0));
    }

    /**
     * getMerchandiseByImei测试
     */
    @Test
    public void should_get_relevant_merchandise_when_giving_imei() {
        Merchandise me = this.merchandiseMapper.getMerchandiseByImei("123456789000012", false , 1);
        assertThat(me.getCategory().getName(), equalTo("X100"));

        me = this.merchandiseMapper.getMerchandiseByImei("9999956789000012", false ,1);
        assertThat(me, nullValue());

        me = this.merchandiseMapper.getMerchandiseByImei("123456789000012", false ,999);
        assertThat(me, nullValue());

        me = this.merchandiseMapper.getMerchandiseByImei("123456789000012", true , 1);
        assertThat(me, nullValue());
    }

    /**
     * getMerchandiseByCateID测试
     */
    @Test
    public void should_get_relevant_list_of_merchandise_when_giving_cate_id() {
        List<Merchandise> mes = this.merchandiseMapper.getMerchandiseByCateID(11, false ,1);
        assertThat(mes.size(), is(5));

        mes = this.merchandiseMapper.getMerchandiseByCateID(999, false, 1);
        assertThat(mes, empty());

        mes = this.merchandiseMapper.getMerchandiseByCateID(11, false, 99);
        assertThat(mes, empty());
    }

    /**
     * insertMerchandise异常情况测试
     */
    @Test
    public void should_throw_exception_when_insert_with_illegal_paras() {
        // cate_id 错误
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                this.merchandiseMapper.insertMerchandise(9999, 999.00, 1500.00,
                "00000000000000001", new Date(), 1, 1));

        // owner_id 错误
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                this.merchandiseMapper.insertMerchandise(12, 999.00, 1500.00,
                        "00000000000000001", new Date(), 999, 1));

        // imei重复 错误
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                this.merchandiseMapper.insertMerchandise(12, 999.00, 1500.00,
                        "123456789000000", new Date(), 1, 1));
    }

    /**
     * insertMerchandise正常测试
     */
    @Test
    public void should_create_me_and_get_its_id_when_inserting() {
        int id = this.merchandiseMapper.insertMerchandise(12, 999.00, 1500.00,
                "00000000000000001", new Date(), 2, 1);
        assertThat(id, greaterThan(2));
        assertThat(this.merchandiseMapper.getMerchandiseById(id, 1), notNullValue());
        assertThat(this.merchandiseMapper.getMerchandiseById(id, 1).getImei(), is("00000000000000001"));
    }


    /**
     * updateSold测试
     */
    @Test
    public void should_update_sold_status_when_update_with_id() {
        int id = this.merchandiseMapper.insertMerchandise(12, 999.00, 1500.00,
                "00000000000000002", new Date(), 1, 1);
        assertThat(this.merchandiseMapper.getMerchandiseById(id, 1).isSold(), is(false));

        this.merchandiseMapper.updateSold(id, true, 1);
        assertThat(this.merchandiseMapper.getMerchandiseById(id, 1).isSold(), is(true));

        // 不存在的id不报错测试
        this.merchandiseMapper.updateSold(99999, true, 1);
        this.merchandiseMapper.updateSold(id, true, 9999);
    }

    /**
     * 删除商品测试
     */
    @Test
    public void should_delete_me_and_orders_when_delete() {
        int id = this.merchandiseMapper.insertMerchandise(12, 999.00, 1500.00,
                "00000000000000033", new Date(), 1, 1);

        this.merchandiseMapper.deleteMerchandise(id, 1);
        assertThat(this.merchandiseMapper.getMerchandiseById(id, 1), nullValue());
    }

    /**
     * 更新商品测试
     */
    @Test
    public void should_update_me_when_update() {
        int id = this.merchandiseMapper.insertMerchandise(12, 999.00, 1500.00,
                "00000000000000044", new Date(), 1, 1);
        this.merchandiseMapper.updateMerchandise(id, 111, 1111, "9999", 1);

        Merchandise result = this.merchandiseMapper.getMerchandiseById(id, 1);
        assertThat(result.getCost(), closeTo(111, 1));
        assertThat(result.getPrice(), closeTo(1111, 1));
        assertThat(result.getImei(), equalTo("9999"));
    }

    /**
     * 搜索测试
     */
    @Test
    public void should_get_mes_when_search() {
        List<Merchandise> result = this.merchandiseMapper.searchMerchandise("notexist", false, 1);
        assertThat(result, empty());
        result = this.merchandiseMapper.searchMerchandise("A97", false,99);
        assertThat(result, empty());

        result = this.merchandiseMapper.searchMerchandise("A97", false,1);
        assertThat(result.size(), greaterThan(1));
        result.forEach(me -> {
            assertThat(me.isSold(), is(false));
        });
    }

    /**
     * countMerchandiseByCateId测试
     */
    @Test
    public void should_get_empty_or_count_when_account() {
        // 已有数据测试
        MeCount meCount = this.merchandiseMapper.countMerchandiseByCateId(11, false ,1);
        assertThat(meCount, notNullValue());
        assertThat(meCount.getCount(), greaterThanOrEqualTo(5));
        assertThat(meCount.getSumCost(), greaterThanOrEqualTo(8750.00));
        assertThat(meCount.getSumPrice(),greaterThanOrEqualTo(9995.00));

        // 非法数据测试
        meCount = this.merchandiseMapper.countMerchandiseByCateId(999, false , 999);
        assertThat(meCount.getCount(), equalTo(0));
    }
}
