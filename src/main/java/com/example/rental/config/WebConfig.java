package com.example.rental.config;

import com.example.rental.interrupt.RentalInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RentalInterceptor())
                .addPathPatterns("/**") // 添加拦截路径，这里配置为拦截所有请求
                .excludePathPatterns("/city")
                .excludePathPatterns("/user/Register/SameAccount")
                .excludePathPatterns("/land/Register/SameAccount")
                .excludePathPatterns("/user/Login")
                .excludePathPatterns("/land/Login")
                .excludePathPatterns("/house/List")
                .excludePathPatterns("/house/List/Type")
                .excludePathPatterns("/house/List/SearchValue")
                .excludePathPatterns("/house/List/City")
                .excludePathPatterns("/comment/HouseId")
                .excludePathPatterns("/house/getHouse/rand")
//                .excludePathPatterns("/websocket")
                .excludePathPatterns("/house/getHouse/*")
                .excludePathPatterns("/Order/getOrder")
                .excludePathPatterns("/Order/payOrder")
                .excludePathPatterns("/city/allLeaderCity");
    }
}
