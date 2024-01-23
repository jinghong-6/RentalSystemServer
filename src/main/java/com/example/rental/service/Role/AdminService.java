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

    //  管理员登录
    Result AdminLogin(String Account,String pwd);
}
