package com.example.rental.controller.Alert;

import com.example.rental.domain.Alert.AdminAlert;
import com.example.rental.service.Alert.AdminAlertService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminAlert")
public class AdminAlertController {
    @Autowired
    AdminAlertService adminAlertService;

    @PostMapping()
    public Result getAllAdminAlert(){
        return adminAlertService.getAllAdminAlert();
    }

    @PostMapping("/detail")
    public Result getAdminAlertById(String AlertId){
        return adminAlertService.getAdminAlertById(AlertId);
    }

    @PostMapping("/read")
    public Result updateConsumerAlertStatus(String AlertId){
        return adminAlertService.updateConsumerAlertStatus(AlertId);
    }

    @PostMapping("/insert")
    public Result InsertAdminAlert(AdminAlert adminAlert){
        return adminAlertService.InsertAdminAlert(adminAlert);
    };
}
