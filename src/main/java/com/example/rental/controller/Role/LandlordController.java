package com.example.rental.controller.Role;

import com.example.rental.domain.Role.Consumer;
import com.example.rental.domain.Role.Landlord;
import com.example.rental.service.Role.LandlordService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/land")
public class LandlordController {
    @Autowired
    private LandlordService landlordService;

    @PostMapping("/Info/getUserInfo")
    public Result getUserInfo(String id){
        return landlordService.getLandInfo(id);
    }

    @PostMapping("/Register/SameAccount")
    public Result getSameAccount(String tele){
        return landlordService.getSameAccount(tele);
    }

    @PostMapping("/Register")
    public Result landlordRegister(Landlord landlord){
        return landlordService.landlordRegister(landlord);
    }

    @PostMapping("/Login")
    public Result landLogin(String tele,String pwd){
        return landlordService.landlordLogin(tele,pwd);
    }

    @PostMapping("/AutoLogin")
    public Result landAutoLogin(String tele){
        return landlordService.landlordAutoLogin(tele);
    }

    @PostMapping("/changeInfo")
    public Result UpdateConsumerInfo(Landlord landlord){
        return landlordService.UpdateLandlordInfo(landlord);
    }
}
