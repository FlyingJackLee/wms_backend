package com.lizumin.wms.service;

import com.lizumin.wms.dao.MerchandiseMapper;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
/**
 * MerchandiseService Test
 *
 * @author Zumin Li
 * @date 2024/2/14 2:14
 */
@ExtendWith(MockitoExtension.class)
public class MerchandiseServiceTest {
    @Mock
    private MerchandiseMapper merchandiseMapper;

    @InjectMocks
    private MerchandiseService merchandiseService;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        User user = new User.Builder().username("test").password("123456").id(1).build();
        this.authentication = UsernamePasswordAuthenticationToken.authenticated(user, "test", List.of());
    }

    /**
     * getMerchandiseByPage正常测试
     */
    @Test
    public void should_call_mapper_when_retrieval_mes() {
        this.merchandiseService.getMerchandiseByPage(authentication, true, 3,5);
        verify(merchandiseMapper, times(1)).getAllMerchandise(1, true, 3, 5);

        this.merchandiseService.getMerchandiseByPage(authentication, 3,5);
        verify(merchandiseMapper, times(1)).getAllMerchandise(1, false, 3, 5);
    }

    /**
     * getMerchandiseByPage异常测试
     */
    @Test
    public void should_throw_exception_when_invalid_paras() {
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getMerchandiseByPage(null, false, 3,5));
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getMerchandiseByPage(authentication, false, -1,5));
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getMerchandiseByPage(authentication, false,1,-1));
    }

    /**
     * insertMerchandise测试
     */
    @Test
    public void should_insert_me_or_throw() {
        // cate id 无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(authentication,-1, 999, 999, "imei", new Date())
        );
        // cost无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(authentication,2, -1, 999, "imei", new Date())
        );
        // price无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(authentication,2, 999, -1, "imei", new Date())
        );
        // imei无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(authentication,1, 999, 999, "", new Date())
        );
        // date无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(authentication,-1, 999, 999, "imei", null)
        );

        Date date = new Date();

        this.merchandiseService.insertMerchandise(authentication,1, 999, 999, "imei", date);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei", date, 1);
    }

    /**
     * insertMerchandise 多串号测试
     */
    @Test
    public void should_insert_multiple_mes() {
        List<String> imeiList = List.of("imei1", "imei2");
        Date date = new Date();
        this.merchandiseService.insertMerchandise(authentication,1, 999, 999, imeiList, date);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei1", date, 1);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei2", date, 1);
    }

    /**
     * getMerchandiseByCateId 测试
     */
    @Test
    public void should_call_mapper_or_throw_with_cate_id() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.getMerchandiseByCateId(authentication, -1, false)
        );

        this.merchandiseService.getMerchandiseByCateId(authentication, 2, true);
        verify(merchandiseMapper, times(1)).getMerchandiseByCateID(2, true, 1);
        this.merchandiseService.getMerchandiseByCateId(authentication, 2);
        verify(merchandiseMapper, times(1)).getMerchandiseByCateID(2, false, 1);
    }

    /**
     * getMerchandiseCount 测试
     */
    @Test
    public void should_get_count() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.getMerchandiseCount(null)
        );

        this.merchandiseService.getMerchandiseCount(authentication, true);
        verify(merchandiseMapper, times(1)).getMerchandiseCount(1, true);

        this.merchandiseService.getMerchandiseCount(authentication);
        verify(merchandiseMapper, times(1)).getMerchandiseCount(1, false);
    }

    /**
     * updateMerchandise 测试
     */
    @Test
    public void should_modify_or_throw_when_update() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(authentication, 0, 999, 1999, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(authentication, 1, -1, 1999, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(authentication, 1, 999, -1, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(authentication, 1, 999, -1, "")
        );

        this.merchandiseService.updateMerchandise(authentication, 1, 999, 1299, "imei");

        verify(merchandiseMapper, times(1)).updateMerchandise(1, 999, 1299, "imei", 1);
    }

    /**
     * deleteMerchandise测试
     */
    @Test
    public void should_remove_or_throw_when_delete() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.deleteMerchandise(authentication, -1)
        );

        this.merchandiseService.deleteMerchandise(authentication, 1);
        verify(merchandiseMapper, times(1)).deleteMerchandise(1, 1);
    }

    /**
     * searchMerchandise测试
     */
    @Test
    public void should_get_me_or_throw_when_search() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.searchMerchandise(authentication, "", true)
        );

        this.merchandiseService.searchMerchandise(authentication, "text", true);
        verify(merchandiseMapper, times(1)).searchMerchandise("text", true, 1);
        this.merchandiseService.searchMerchandise(authentication, "text");
        verify(merchandiseMapper, times(1)).searchMerchandise("text", false, 1);
    }

    /**
     * updateSold测试
     */
    @Test
    public void should_set_sold_or_throw_when_update_sold() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateSold(authentication, 0, true)
        );

        this.merchandiseService.updateSold(authentication, 2, true);
        verify(merchandiseMapper, times(1)).updateSold(2,true, 1);
    }

    /**
     * getMerchandiseById测试
     */
    @Test
    public void should_get_me_with_id() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.getMerchandiseById(authentication, 0)
        );

        this.merchandiseService.getMerchandiseById(authentication, 1);
        verify(merchandiseMapper, times(1)).getMerchandiseById(1,1);
    }
}


