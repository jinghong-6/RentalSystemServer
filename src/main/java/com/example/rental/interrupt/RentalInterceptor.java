package com.example.rental.interrupt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.rental.domain.Role.Consumer;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static com.example.rental.utils.JWTUtils.verify;

public class RentalInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        return HandlerInterceptor.super.preHandle(request, response, handler);
        System.out.println("被拦截1");

        // 1. 获取请求头中的token
        String token = request.getHeader(AUTHORIZATION_HEADER);

        if (token != null) {
            // 2. 解析token
            String jwtToken = token.replace(TOKEN_PREFIX, "");
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            // 获取token类型
            String tokenType = decodedJWT.getClaim("tokenType").asString();

            // 根据token类型执行相应的校验逻辑
            if ("register".equals(tokenType)) {
                // 执行register的校验逻辑
                String userType = decodedJWT.getClaim("userType").asString();
                String tele = decodedJWT.getClaim("tele").asString();
                Date expiresAt = decodedJWT.getExpiresAt();
                Date currentTime = new Date();
//              为true则令牌过期
                if (currentTime.after(expiresAt)) {
                    sendErrorResponse(response);
                    return false;
                } else {
//                  判断注册的账号是否和token中的一样
                    if (tele.equals(request.getParameter("tele")) && verify(token)) {
                        return true;
                    } else {
                        sendErrorResponse(response);
                        return false;
                    }
                }
            } else if ("login".equals(tokenType)) {
                // 执行login的校验逻辑
                Date expiresAt = decodedJWT.getExpiresAt();
                Date currentTime = new Date();
//                String tele = decodedJWT.getClaim("tele").asString();
                // 判断token是否过期
                if (currentTime.after(expiresAt)){
                    sendTokenTimeOutResponse(response);
                    return false;
                }else {
                    // 判断token是否正确
                    if (verify(token)){
                        return true;
                    }else {
                        sendErrorResponse(response);
                        return false;
                    }
                }
            } else {
                sendErrorResponse(response);
                return false;
            }
        } else {
            if (request.getParameter("tele") != null && request.getParameter("pwd") != null){
                System.out.println("访问登录接口");
                return true;
            }else {
                sendErrorResponse(response);
                return false;
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    //    封装失败返回值
    public static void sendErrorResponse(HttpServletResponse response) throws IOException {
        Result failedResponse = new Result(Code.SEARCH_ERR, 500);
        String json = JSON.toJSONString(failedResponse);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    //    封装token超时返回值
    public static void sendTokenTimeOutResponse(HttpServletResponse response) throws IOException {
        Result failedResponse = new Result(Code.SEARCH_ERR, 5000);
        String json = JSON.toJSONString(failedResponse);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}


