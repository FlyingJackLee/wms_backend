package com.lizumin.wms.tools;

import com.lizumin.wms.tool.JwtTokenTool;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.lizumin.wms.tool.JwtTokenTool.generateToken;
import static com.lizumin.wms.tool.JwtTokenTool.verifyAndGetClaims;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JwtTokenToolTest {
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    @BeforeAll
    public static void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field privateKeyField = JwtTokenTool.class.getDeclaredField("privateKey");
        privateKeyField.setAccessible(true);
        privateKey = (PrivateKey) privateKeyField.get(null);
        Field publicKeyField = JwtTokenTool.class.getDeclaredField("publicKey");
        publicKeyField.setAccessible(true);
        publicKey = (PublicKey) publicKeyField.get(null);
    }

    /**
     * 通过签名数据，测试读取的RSA密钥对是否正确
     *
     */
    @Test
    public void should_have_legal_rsa_key_pair_when_class_initialize() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException, InvalidKeyException, SignatureException {
        byte[] challenge = new byte[10000];
        ThreadLocalRandom.current().nextBytes(challenge);

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(challenge);
        byte[] signature = sig.sign();

        sig.initVerify(publicKey);
        sig.update(challenge);

        assertThat(sig.verify(signature), is(true));
    }

    /**
     * 测试jwt生成
     *
     */
    @Test
    public void should_jwt_with_audience_when_generate_token_with_or_without_expire() {
        String token = generateToken("test001", 1);
        Optional<Jws<Claims>> claimsJws = verifyAndGetClaims(token);

        assertThat(claimsJws.isEmpty(),is(false));
        assertThat(claimsJws.get().getPayload().get("id"), is(1));
        assertThat(claimsJws.get().getPayload().getAudience(), hasItem("test001"));

        Date tokenDate = claimsJws.get().getPayload().getExpiration();
        assertThat(diffMill(new Date(), tokenDate), equalTo(23L));

        Calendar calendar = Calendar.getInstance();
        // 默认有效期
        calendar.add(Calendar.HOUR_OF_DAY, 72);
        token = generateToken("test001", 1, calendar.getTime());
        claimsJws = verifyAndGetClaims(token);
        tokenDate = claimsJws.get().getPayload().getExpiration();
        assertThat(diffMill(new Date(), tokenDate), equalTo(71L));
    }

    /**
     * 测试token为空时, 验证token
     *
     */
    @Test
    public void should_get_empty_when_token_is_illegal() {
        String token = null;
        Optional<Jws<Claims>> claimsJws = verifyAndGetClaims(token);
        assertThat(claimsJws.isEmpty(),is(true));

        token = "";
        claimsJws = verifyAndGetClaims(token);
        assertThat(claimsJws.isEmpty(),is(true));

        token = "not.areal.token";
        claimsJws = verifyAndGetClaims(token);
        assertThat(claimsJws.isEmpty(),is(true));
    }

    private long diffMill(Date d1, Date d2) {
        long diffInMill = d2.getTime() - d1.getTime();
        return TimeUnit.HOURS.convert(diffInMill, TimeUnit.MILLISECONDS);
    }

}
