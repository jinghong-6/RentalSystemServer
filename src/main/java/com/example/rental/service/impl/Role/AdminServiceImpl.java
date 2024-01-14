package com.example.rental.service.impl.Role;

import com.alibaba.fastjson.JSONObject;
import com.example.rental.dao.Role.AdminDao;
import com.example.rental.domain.Role.Admin;
import com.example.rental.service.Role.AdminService;
import com.example.rental.utils.Code;
import com.example.rental.utils.JWTUtils;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminDao adminDao;

    @Override
    public Result getAdminInfo(String Account) {
        Admin admin = adminDao.getAdminInfoByAccount(Account);

        if (admin != null){
            //    获取token
            String token = JWTUtils.getAdminToken("admin",admin.getAdmin_name());
            JSONObject json = new JSONObject();
            json.put("admin",admin);
            json.put("token",token);
            return new Result(Code.SEARCH_OK,json);
        }else {
            return new Result(Code.SEARCH_ERR,"未查询到相关信息");
        }
    }

    @Override
    public Result AdminLogin(String Account, String pwd) {
        if (adminDao.getPwdByAccount(Account) != null && adminDao.getPwdByAccount(Account).equals(pwd)){
            Admin admin = adminDao.getAdminInfoByAccount(Account);

            if (admin != null){
                //    获取token
                String token = JWTUtils.getAdminToken("admin",admin.getAdmin_name());
                JSONObject json = new JSONObject();
                json.put("admin",admin);
                json.put("token",token);
                return new Result(Code.SEARCH_OK,json);
            }else {
                return new Result(Code.SEARCH_ERR,"登录失败");
            }
        }else {
            return new Result(Code.SEARCH_ERR,"登录失败");
        }
    }
}
