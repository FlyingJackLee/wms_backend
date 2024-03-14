package com.lizumin.wms.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static com.lizumin.wms.tool.JsonTool.objToJson;
import static com.lizumin.wms.tool.JsonTool.jsonToObj;

public class JsonToolTest {
    private static User user;
    private static String userJson = "{\"id\":1,\"password\":\"12346\",\"username\":null,\"enabled\":true,\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"authorities\":[{\"@class\":\"com.lizumin.wms.entity.SystemAuthority\",\"authority\":\"ROLE_ADMIN\"},{\"@class\":\"com.lizumin.wms.entity.SystemAuthority\",\"authority\":\"READ_ONLY\"}],\"group\":{\"id\":0,\"storeName\":null,\"address\":null,\"contact\":null}}";

    @BeforeAll
    public static void setUp(){
        Set<GrantedAuthority> authorities = new HashSet<>(2);
        authorities.add(new SystemAuthority("ROLE_ADMIN"));
        authorities.add(new SystemAuthority("READ_ONLY"));
        user = new User.Builder().id(1).password("12346").authorities(authorities).build();
    }

    @Test
    public void should_return_json_string_when_target_is_legal_user() throws JsonProcessingException {
        assertThat(objToJson(user), equalTo(userJson));
        assertThat(jsonToObj(userJson, User.class), equalTo(user));
    }
}
