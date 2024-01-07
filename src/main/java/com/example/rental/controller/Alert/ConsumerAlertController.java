package com.example.rental.controller.Alert;

import com.example.rental.service.Alert.ConsumerAlertService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumerAlert")
public class ConsumerAlertController {
    @Autowired
    ConsumerAlertService consumerAlertService;

    @PostMapping()
    public Result getAlertByConsumerId(String ConsumerId){
        return consumerAlertService.getAlertByConsumerId(ConsumerId);
    }

    @PostMapping("/getAlert")
    public Result getAlertByConsumerIdAndAlertId(String ConsumerId,String AlertId){
        return consumerAlertService.getAlertByConsumerIdAndAlertId(ConsumerId,AlertId);
    }

    @PostMapping("/read")
    public Result readAlertByConsumerIdAndAlertId(String ConsumerId,String AlertId){
        return consumerAlertService.updateConsumerAlertStatus(ConsumerId,AlertId);
    }

    @PostMapping("/getCount")
    public Result getAlertCountByConsumerId(String ConsumerId){
        return consumerAlertService.getAlertCountByConsumerId(ConsumerId);
    }
}
