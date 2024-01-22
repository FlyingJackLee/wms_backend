package com.lizumin.wms.tools;

import org.junit.jupiter.api.Test;

import static com.lizumin.wms.tool.Verify.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class VerifyTest {
    @Test
    public void should_return_true_when_parameter_legal() {
        assertThat(verifyUsername("test001"), equalTo(true));
        assertThat(verifyPassword("test001.~!"), equalTo(true));
        assertThat(verifyEmail("test001@test.com"), equalTo(true));
        assertThat(verifyPhoneNumber("13812341234"), equalTo(true));
        assertThat(isNotBlank("test001"), equalTo(true));
    }

    @Test
    public void should_return_true_when_target_is_null(){
        assertThat(isNotBlank(null), equalTo(false));
        assertThat(isNotBlank(""), equalTo(false));
        assertThat(isNotBlank("    "), equalTo(false));
    }

    /**
     * username不合法时测试
     *
     */
    @Test
    public void should_return_false_when_username_is_illegal() {
        assertThat(verifyUsername(""), equalTo(false));
        assertThat(verifyUsername(null), equalTo(false));
        assertThat(verifyUsername("asc"), equalTo(false));
        assertThat(verifyUsername("ascxzc.~!@#$%^"), equalTo(false));
        assertThat(verifyUsername("ascxzc123ascs123saca123"), equalTo(false));
    }

    /**
     * email不合法时测试
     *
     */
    @Test
    public void should_return_false_when_email_is_illegal() {
        assertThat(verifyEmail(""), equalTo(false));
        assertThat(verifyEmail(null), equalTo(false));
        assertThat(verifyEmail("asc"), equalTo(false));
        assertThat(verifyEmail("ascxzc@"), equalTo(false));
    }

    /**
     * Password不合法时测试
     *
     */
    @Test
    public void should_return_false_when_password_is_illegal() {
        assertThat(verifyPassword(""), equalTo(false));
        assertThat(verifyPassword(null), equalTo(false));
        assertThat(verifyPassword("asc.21A"), equalTo(false));
        assertThat(verifyPassword("ascxzcdasdasdsadsadsadsadsasdaqwezxc@"), equalTo(false));
    }

    /**
     * phone不合法时测试
     *
     */
    @Test
    public void should_return_false_when_phone_number_is_illegal() {
        assertThat(verifyPhoneNumber(""), equalTo(false));
        assertThat(verifyPhoneNumber(null), equalTo(false));
        assertThat(verifyPhoneNumber("asc.21A"), equalTo(false));
        assertThat(verifyPhoneNumber("1236545"), equalTo(false));
    }

    /**
     * phone不合法时测试
     *
     */
    @Test
    public void should_return_true_when_parameter_is_illegal() {
        assertThat(verifyPhoneNumber(""), equalTo(false));
        assertThat(verifyPhoneNumber(null), equalTo(false));
        assertThat(verifyPhoneNumber("asc.21A"), equalTo(false));
        assertThat(verifyPhoneNumber("1236545"), equalTo(false));
    }
}
