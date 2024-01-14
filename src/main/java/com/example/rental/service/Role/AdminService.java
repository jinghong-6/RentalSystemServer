package com.example.rental.service.Role;

import com.example.rental.utils.Result;

public interface AdminService {
    //  获取管理员信息
    Result getAdminInfo(String Account);

    //  管理员登录
    Result AdminLogin(String Account,String pwd);
}
