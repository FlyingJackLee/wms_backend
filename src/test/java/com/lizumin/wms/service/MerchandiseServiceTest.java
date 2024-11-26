package com.lizumin.wms.service;

import com.lizumin.wms.dao.MerchandiseMapper;
import com.lizumin.wms.entity.Category;
import com.lizumin.wms.entity.MeCount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * MerchandiseService Test
 *
 * @author Zumin Li
 * @date 2024/2/14 2:14
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class MerchandiseServiceTest {
    @MockBean
    private MerchandiseMapper merchandiseMapper;

    @Autowired
    private MerchandiseService merchandiseService;

    /**
     * getMerchandiseByPage测试 - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_root_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.getMerchandiseByPage(true, 3,5);
        });
    }

    /**
     * getMerchandiseByPage正常测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_call_mapper_when_retrieval_mes_with_staff() {
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getMerchandiseByPage(false, -1,5));
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getMerchandiseByPage(false,1,-1));

        this.merchandiseService.getMerchandiseByPage(true, 3,5);
        verify(merchandiseMapper, times(1)).getAllMerchandise(1, true, 3, 5);

        this.merchandiseService.getMerchandiseByPage(3,5);
        verify(merchandiseMapper, times(1)).getAllMerchandise(1, false, 3, 5);
    }

    /**
     * getMerchandiseByPage正常测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_call_mapper_when_retrieval_mes_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getMerchandiseByPage(false, -1,5));
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getMerchandiseByPage(false,1,-1));

        this.merchandiseService.getMerchandiseByPage(true, 3,5);
        verify(merchandiseMapper, times(1)).getAllMerchandise(1, true, 3, 5);

        this.merchandiseService.getMerchandiseByPage(3,5);
        verify(merchandiseMapper, times(1)).getAllMerchandise(1, false, 3, 5);
    }

    /**
     * insertMerchandise测试- DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_insert_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.insertMerchandise(2, 999, 999, "imei", new Date());
        });
    }

    /**
     * insertMerchandise测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_insert_me_or_throw() {
        // cate id 无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(-1, 999, 999, "imei", new Date())
        );
        // cost无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(2, -1, 999, "imei", new Date())
        );
        // price无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(2, 999, -1, "imei", new Date())
        );
        // imei无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(1, 999, 999, "", new Date())
        );
        // date无效
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.insertMerchandise(-1, 999, 999, "imei", null)
        );

        Date date = new Date();

        this.merchandiseService.insertMerchandise(1, 999, 999, "imei", date);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei", date, 1, 1);
    }

    /**
     * insertMerchandise 多串号测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_insert_multiple_mes() {
        List<String> imeiList = List.of("imei1", "imei2");
        Date date = new Date();
        this.merchandiseService.insertMerchandise(1, 999, 999, imeiList, date);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei1", date, 1, 1);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei2", date, 1, 1);
    }

    /**
     * insertMerchandise测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_insert_me_when_insert_with_owner() {
        Date date = new Date();
        this.merchandiseService.insertMerchandise(1, 999, 999, "imei", date);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei", date, 1, 1);
    }

    /**
     * insertMerchandise 多串号测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_insert_mes_when_insert_with_owner() {
        List<String> imeiList = List.of("imei1", "imei2");
        Date date = new Date();
        this.merchandiseService.insertMerchandise(1, 999, 999, imeiList, date);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei1", date, 1, 1);
        verify(merchandiseMapper, times(1)).insertMerchandise(1, 999, 999, "imei2", date, 1, 1);
    }

    /**
     * getMerchandiseByCateId测试 - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_by_cate_id_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.getMerchandiseByCateId(-1, false);
        });
    }

    /**
     * getMerchandiseByCateId 测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_call_mapper_or_throw_with_cate_id() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.merchandiseService.getMerchandiseByCateId(-1, false)
        );

        this.merchandiseService.getMerchandiseByCateId(2, true);
        verify(merchandiseMapper, times(1)).getMerchandiseByCateID(2, true, 1);
        this.merchandiseService.getMerchandiseByCateId(2);
        verify(merchandiseMapper, times(1)).getMerchandiseByCateID(2, false, 1);
    }

    /**
     * getMerchandiseByCateId 测试 - OWNER
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_call_mapper_or_throw_with_cate_id_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.merchandiseService.getMerchandiseByCateId(-1, false)
        );

        this.merchandiseService.getMerchandiseByCateId(2, true);
        verify(merchandiseMapper, times(1)).getMerchandiseByCateID(2, true, 1);
        this.merchandiseService.getMerchandiseByCateId(2);
        verify(merchandiseMapper, times(1)).getMerchandiseByCateID(2, false, 1);
    }

    /**
     * getMerchandiseCount - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_count_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.getMerchandiseByCateId(-1, false);
        });
    }

    /**
     * getMerchandiseCount 测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_get_count() {
        this.merchandiseService.getMerchandiseCount(true);
        verify(merchandiseMapper, times(1)).getMerchandiseCount(1, true);

        this.merchandiseService.getMerchandiseCount();
        verify(merchandiseMapper, times(1)).getMerchandiseCount(1, false);
    }

    /**
     * getMerchandiseCount 测试 - OWNER
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_get_count_with_owner() {
        this.merchandiseService.getMerchandiseCount(true);
        verify(merchandiseMapper, times(1)).getMerchandiseCount(1, true);

        this.merchandiseService.getMerchandiseCount();
        verify(merchandiseMapper, times(1)).getMerchandiseCount(1, false);
    }

    /**
     * getMerchandiseById测试 - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_by_id_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.getMerchandiseById(3);
        });
    }

    /**
     * getMerchandiseById测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_get_me_with_id() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.getMerchandiseById( 0)
        );

        this.merchandiseService.getMerchandiseById( 1);
        verify(merchandiseMapper, times(1)).getMerchandiseById(1,1);
    }

    /**
     * getMerchandiseById测试 - OWNER
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_get_me_with_id_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.getMerchandiseById( 0)
        );

        this.merchandiseService.getMerchandiseById( 1);
        verify(merchandiseMapper, times(1)).getMerchandiseById(1,1);
    }

    /**
     * updateMerchandise - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_update_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.updateMerchandise(1, 999, 1299, "imei");
        });
    }

    /**
     * updateMerchandise 测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_modify_or_throw_when_update() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(0, 999, 1999, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(1, -1, 1999, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(1, 999, -1, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(1, 999, -1, "")
        );

        this.merchandiseService.updateMerchandise(1, 999, 1299, "imei");
        verify(merchandiseMapper, times(1)).updateMerchandise(1, 999, 1299, "imei", 1);
    }

    /**
     * updateMerchandise 测试 - OWNER
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_modify_or_throw_when_update_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(0, 999, 1999, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(1, -1, 1999, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(1, 999, -1, "imei")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateMerchandise(1, 999, -1, "")
        );

        this.merchandiseService.updateMerchandise(1, 999, 1299, "imei");
        verify(merchandiseMapper, times(1)).updateMerchandise(1, 999, 1299, "imei", 1);
    }

    /**
     * deleteMerchandise测试 - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_delete_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.deleteMerchandise( 3);
        });
    }

    /**
     * updateMerchandise 测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_remove_or_throw_when_delete() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.deleteMerchandise( -1)
        );

        this.merchandiseService.deleteMerchandise(1);
        verify(merchandiseMapper, times(1)).deleteMerchandise(1, 1);
    }

    /**
     * updateMerchandise 测试 - OWNER
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_remove_or_throw_when_delete_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.deleteMerchandise( -1)
        );

        this.merchandiseService.deleteMerchandise(1);
        verify(merchandiseMapper, times(1)).deleteMerchandise(1, 1);
    }

    /**
     * searchMerchandise测试 - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_search_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.searchMerchandise("text", true);
        });
    }

    /**
     * searchMerchandise测试- STAFF
     *
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_get_me_or_throw_when_search() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.searchMerchandise( "", true)
        );

        this.merchandiseService.searchMerchandise("text", true);
        verify(merchandiseMapper, times(1)).searchMerchandise("text", true, 1);
        this.merchandiseService.searchMerchandise("text");
        verify(merchandiseMapper, times(1)).searchMerchandise("text", false, 1);
    }

    /**
     * searchMerchandise测试- OWNER
     *
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_get_me_or_throw_when_search_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.searchMerchandise( "", true)
        );

        this.merchandiseService.searchMerchandise("text", true);
        verify(merchandiseMapper, times(1)).searchMerchandise("text", true, 1);
        this.merchandiseService.searchMerchandise("text");
    }

    /**
     * searchMerchandise测试 - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_update_sold_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.updateSold(2, true);
        });
    }

    /**
     * updateSold测试 - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_set_sold_or_throw_when_update_sold() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateSold(0, true)
        );

        this.merchandiseService.updateSold(2, true);
        verify(merchandiseMapper, times(1)).updateSold(2,true, 1);
    }

    /**
     * updateSold测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_set_sold_or_throw_when_update_sold_with_OWNER() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.merchandiseService.updateSold(0, true)
        );

        this.merchandiseService.updateSold(2, true);
        verify(merchandiseMapper, times(1)).updateSold(2,true, 1);
    }

    /**
     * accountMerchandises测试 - DEFAULT
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_accout_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            this.merchandiseService.accountMerchandises();
        });
    }
}


