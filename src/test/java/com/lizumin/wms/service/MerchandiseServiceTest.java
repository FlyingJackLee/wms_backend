package com.lizumin.wms.service;

import com.lizumin.wms.dao.MerchandiseMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    /**
     * getNonSoldMerchandise正常测试
     */
    @Test
    public void should_call_mapper_when_retrieval_mes() {
        this.merchandiseService.getNonSoldMerchandise(1, 3,5);
        verify(merchandiseMapper, times(1)).getAllMerchandiseBySold(1, false, 3, 5);
    }

    /**
     * getNonSoldMerchandise异常测试
     */
    @Test
    public void should_throw_exception_when_invalid_paras() {
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getNonSoldMerchandise(-1, 3,5));
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getNonSoldMerchandise(1, 0,5));
        Assertions.assertThrows(IllegalArgumentException.class,() ->  this.merchandiseService.getNonSoldMerchandise(1, 1,-1));
    }
}
