package com.lizumin.wms.tools;

import com.lizumin.wms.tool.JwtTokenTool;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static com.lizumin.wms.tool.JwtTokenTool.generateToken;
import static com.lizumin.wms.tool.JwtTokenTool.verifyAndGetClaims;

public class JwtTokenToolTest {
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private String testAudience = "test";

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
        String token = generateToken(testAudience);
        Optional<Jws<Claims>> claimsJws = verifyAndGetClaims(token);

        assertThat(claimsJws.isEmpty(),is(false));
        assertThat(claimsJws.get().getPayload().getAudience(), hasItem(testAudience));
        Date tokenDate = claimsJws.get().getPayload().getExpiration();
        assertThat(diffMill(new Date(), tokenDate), equalTo(23L));

        Calendar calendar = Calendar.getInstance();
        // 默认有效期
        calendar.add(Calendar.HOUR_OF_DAY, 72);
        token = generateToken(testAudience, calendar.getTime());
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

    /**
     * 测试token合法时, 验证token
     *
     */
    @Test
    public void should_get_claims_when_token_is_legal() throws ParseException {
        // 利用key手动生成的，如果key变化需要修改测试token(2124年过期)
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJhdWQiOlsidGVzdCJdLCJleHAiOjQ4NjE1MjkzOTAsImlhdCI6MTcwNTg1NTc5MH0.h1mpQPTdxVPOyYhGYIcp6twhY7IJ1oTwZ93lqe3_tONRqsFKSDdKijjQNhjjcqbVCxEX3lnU8WE-WUJAaBVkzAc1bKDjF0RZKX-KA0sErdu20I6U37dg8h50JGLx8h36ibP7aTO-jNmAB9Mpz_kbSCEW5gy1-PMUK9LShoNzs8ZrBZ9RA0CLd_xXZ5KByPXuNIRMwAXnse82GuBm2_S3E0oUGf7mXW28oRu31nZ4mEPe0AD7dz7yV_Vh4atHkSWgtq5weWR0hromVArx3a9hhxvP802J_6cP7W5jfTe8XgEBhi5xJ9R6DpbTRyDfx809Sa76kK1RKu-q8dsDvCcX5g";
        Optional<Jws<Claims>> claimsJws = verifyAndGetClaims(token);
        assertThat(claimsJws.isPresent(),is(true));
        assertThat(claimsJws.get().getPayload().getAudience(), hasItem("test"));

        long tokenDate = claimsJws.get().getPayload().getExpiration().getTime();
        assertThat(tokenDate, is(4861529390000L));
    }

    private long diffMill(Date d1, Date d2) {
        long diffInMill = d2.getTime() - d1.getTime();
        return TimeUnit.HOURS.convert(diffInMill, TimeUnit.MILLISECONDS);
    }

}
