package com.example.rental.controller.Alert;

import com.example.rental.service.Alert.LandlordAlertService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/LandlordAlert")
public class LandlordAlertController {
    @Autowired
    LandlordAlertService landlordAlertService;

    @PostMapping()
    public Result getAlertByLandlordId(String LandlordId){
        return landlordAlertService.getAlertByLandlordId(LandlordId);
    }

    @PostMapping("/getAlert")
    public Result getAlertByLandlordIdAndAlertId(String LandlordId,String AlertId){
        return landlordAlertService.getAlertByLandlordIdAndAlertId(LandlordId,AlertId);
    }

    @PostMapping("/read")
    public Result readAlertByLandlordIdAndAlertId(String LandlordId,String AlertId){
        return landlordAlertService.updateLandlordAlertStatus(LandlordId,AlertId);
    }

    @PostMapping("/getCount")
    public Result getAlertCountByLandlordId(String LandlordId){
        return landlordAlertService.getAlertCountByLandlordId(LandlordId);
    }
}
