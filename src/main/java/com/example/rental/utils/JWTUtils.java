package com.example.rental.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTUtils {
    private static String SING;
    private static Integer expireTime;

    public void setSING(String SING) {
        JWTUtils.SING = SING;
    }

    public void setExpireTime(Integer expireTime) {
        JWTUtils.expireTime = expireTime;
    }

    //    登录后access Token，短token
    public static String getLoginAccessToken(String tele, String userType, String username) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 30);

        JWTCreator.Builder builder = JWT.create();

        Map<String, String> payload = new HashMap<>();
        payload.put("tokenType", "login");
        payload.put("tele", tele);
        payload.put("name", username);
        payload.put("userType", userType);
        payload.forEach(builder::withClaim);
        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SING));
        return token;
    }

    //    登录后refresh Token，长token
    public static String getLoginRefreshToken(String tele, String userType, String username) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 60*24*2);

        JWTCreator.Builder builder = JWT.create();

        Map<String, String> payload = new HashMap<>();
        payload.put("tokenType", "login");
        payload.put("tele", tele);
        payload.put("name", username);
        payload.put("userType", userType);
        payload.forEach(builder::withClaim);
        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SING));
        return token;
    }

    //    注册用token
    public static String getRegisterToken(String tele, String userType) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, expireTime);

        JWTCreator.Builder builder = JWT.create();

        Map<String, String> payload = new HashMap<>();
        payload.put("tokenType", "register");
        payload.put("tele", tele);
        payload.put("userType", userType);
        payload.forEach(builder::withClaim);
        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SING));
        return token;
    }

    public static boolean verify(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
//            验证失败
            return false;
        }
    }
}
