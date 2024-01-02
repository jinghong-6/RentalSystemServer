package com.example.rental.controller.Role;

import com.example.rental.domain.Role.Consumer;
import com.example.rental.service.Role.ConsumerService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class ConsumerController {
    @Autowired
    private ConsumerService consumerService;

    @PostMapping("/Info/getUserInfo")
    public Result getUserInfo(String id){
        return consumerService.getUserInfo(id);
    }

    @PostMapping("/Register/SameAccount")
    public Result getSameAccount(String tele){
        return consumerService.getSameAccount(tele);
    }

    @PostMapping("/Register")
    public Result userRegister(Consumer consumer){
        return consumerService.userRegister(consumer);
    }

    @PostMapping("/Login")
    public Result userLogin(String tele,String pwd){
        return consumerService.userLogin(tele,pwd);
    }

    @PostMapping("/AutoLogin")
    public Result userAutoLogin(String tele){
        return consumerService.userAutoLogin(tele);
    }

    @PostMapping("/changeInfo")
    public Result UpdateConsumerInfo(Consumer consumer ){
        return consumerService.UpdateConsumerInfo(consumer);
    }
}
