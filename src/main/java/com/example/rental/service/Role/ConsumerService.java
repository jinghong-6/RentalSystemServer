package com.example.rental.service.Role;

import com.example.rental.domain.Role.Consumer;
import com.example.rental.utils.Result;

public interface ConsumerService {
    //  获取用户信息
    Result getUserInfo(String id);

    //  判断是否有相同账号
    Result getSameAccount(String tele);

    //  用户注册
    Result userRegister(Consumer consumer);

    //  用户登录
    Result userLogin(String tele,String pwd);

    //  用户自动登录
    Result userAutoLogin(String tele);

    //  修改用户信息
    Result UpdateConsumerInfo(Consumer consumer);
}
