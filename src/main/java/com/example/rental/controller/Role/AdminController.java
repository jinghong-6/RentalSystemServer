package com.example.rental.controller.Role;

import com.example.rental.service.Role.AdminService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/Login")
    public Result userLogin(String Account, String pwd){
        return adminService.AdminLogin(Account,pwd);
    }

    @PostMapping("/Info/getAdminInfo")
    public Result getAdminInfo(String Account){
        return adminService.getAdminInfo(Account);
    }
}
