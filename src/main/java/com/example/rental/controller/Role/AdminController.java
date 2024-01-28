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

    @PostMapping("/getHouse")
    public Result getAdminHouse(){
        return adminService.getHouseByAdmin();
    }

    @PostMapping("/UpdateHouse")
    public Result UpdateAdminHouse(String status,String id){
        return adminService.UpdateHouseStatus(status,id);
    }

    @PostMapping("/getAllConsumer")
    public Result getAllConsumer(){
        return adminService.getAllConsumer();
    }

    @PostMapping("/updateConsumer")
    public Result updateConsumerById(String status,String id){
        return adminService.UpdateConsumerStatusById(status,id);
    }

    @PostMapping("/getAllLandlord")
    public Result getAllLandlord(){
        return adminService.getAllLandlord();
    }

    @PostMapping("/updateLandlord")
    public Result updateLandlordById(String status,String id){
        return adminService.UpdateLandlordStatusById(status,id);
    }

    @PostMapping("/getAllInfo")
    public Result getAllInfo(){
        return adminService.getAllInfo();
    }
}
