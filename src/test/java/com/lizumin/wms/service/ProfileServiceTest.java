package com.lizumin.wms.service;

import com.lizumin.wms.dao.UserMapper;
import com.lizumin.wms.entity.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;

/**
 * @author Zumin Li
 * @date 2024/3/16 14:59
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class ProfileServiceTest {
    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ProfileService profileService;

    /**
     * getProfile测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_call_mapper_when_getting() {
        UserProfile profile = this.profileService.getProfile();
        assertThat(profile.getNickname(), equalTo(UserProfile.defaults(1).getNickname()));

        UserProfile profileStub = UserProfile.defaults(1);
        Mockito.when(this.userMapper.getProfile(anyInt())).thenReturn(profileStub);

        profile = this.profileService.getProfile();
        assertThat(profile, is(profileStub));
    }

    /**
     * updateNickname  updateEmail updatePhone测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_throw_or_call_mapper_when__update() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.profileService.updateNickname("");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.profileService.updateEmail("aa");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.profileService.updatePhone("a1");
        });

        this.profileService.updateNickname("test");
        verify(this.userMapper).updateNickname(1, "test");

        this.profileService.updateEmail("testprofile@test.com");
        verify(this.userMapper).updateEmail(1, "testprofile@test.com");

        this.profileService.updateNickname("10012341234");
        verify(this.userMapper).updateNickname(1, "10012341234");
    }
}
