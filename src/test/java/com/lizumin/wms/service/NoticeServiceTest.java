package com.lizumin.wms.service;

import com.lizumin.wms.dao.NoticeMapper;
import com.lizumin.wms.entity.Notice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author Zumin Li
 * @date 2024/3/10 23:51
 */
@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {
    @Mock
    private NoticeMapper noticeMapper;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    public void should_throw_exception_when_invalid_role() {
        Assertions.assertThrows(IllegalArgumentException.class,() -> {
            this.noticeService.latest(null, "update");
        });
    }

    @Test
    public void should_get_call_mapper_with_relating_paras() {
        // null test
        Mockito.when(noticeMapper.getNoticeByType("")).thenReturn(null);
        Notice result = this.noticeService.latest(Mockito.mock(Authentication.class), null);
        Mockito.verify(noticeMapper).getNoticeByType("");

        // normal test
        Notice update = Mockito.mock(Notice.class);
        Mockito.when(noticeMapper.getNoticeByType("update")).thenReturn(update);
        result = this.noticeService.latest(Mockito.mock(Authentication.class), "update");
        Mockito.verify(noticeMapper).getNoticeByType("update");
        assertThat(result, equalTo(result));
    }
}
