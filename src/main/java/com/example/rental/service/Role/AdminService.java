package com.example.rental.service.Role;

import com.example.rental.utils.Result;

public interface AdminService {
    //  获取管理员信息
    Result getAdminInfo(String Account);

    //  获取网站信息
    Result getAllInfo();

    //  获取民宿列表
    Result getHouseByAdmin();

    //  设置民宿状态
    Result UpdateHouseStatus(String status, String id);

    //  获取所有用户信息
    Result getAllConsumer();

    //  修改用户状态
    Result UpdateConsumerStatusById(String status,String id);

    //  获取所有房东信息
    Result getAllLandlord();

    //  修改房东状态
    Result UpdateLandlordStatusById(String status,String id);

    //  管理员登录
    Result AdminLogin(String Account,String pwd);
}
