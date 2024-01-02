package com.example.rental.service.Role;

import com.example.rental.domain.Role.Consumer;
import com.example.rental.domain.Role.Landlord;
import com.example.rental.utils.Result;

public interface LandlordService {
    //    获取房东信息
    Result getLandInfo(String id);

    //    判断是否有相同账号
    Result getSameAccount(String tele);

    //    用户注册
    Result landlordRegister(Landlord landlord);

    //    用户登录
    Result landlordLogin(String tele,String pwd);

    //    用户自动登录
    Result landlordAutoLogin(String tele);

    //  修改用户信息
    Result UpdateLandlordInfo(Landlord landlord);
}
