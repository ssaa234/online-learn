package com.ape.apecommon.utils;

import com.ape.apecommon.constant.Constants;
import com.ape.apecommon.utils.spring.SpringUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

public class JwtUtil {
    public static final long EXPIRE_TIME = 24 * 3 * 60 * 60 * 1000;
    public static final int days = 3;
    private static StringRedisTemplate stringRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);

    public static boolean verify(String token, String userId, String userPhone) {
        try {
            // 根据密码生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(userPhone);
            JWTVerifier verifier = JWT.require(algorithm).withClaim("userId", userId).build();
            // 效验TOKEN
            verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String sign(String userId, String userPhone) {
        Algorithm algorithm = Algorithm.HMAC256(userPhone);
        // 附带userId信息  可以将user信息转成map存到这里
        //String token = JWT.create().withClaim("userId", userId).withExpiresAt(date).sign(algorithm);
        String token = JWT.create().withClaim("userId", userId).sign(algorithm);
        stringRedisTemplate.opsForValue().set(Constants.PREFIX_USER_TOKEN + userId, token, days, TimeUnit.DAYS);
        return token;

    }

    public static String getUserIdByToken(HttpServletRequest request) {
        String accessToken = getTokenByRequest(request);
        return getUserId(accessToken);
    }

    public static String getTokenByRequest(HttpServletRequest request) {
        return request.getHeader(Constants.X_ACCESS_TOKEN);
    }
}

