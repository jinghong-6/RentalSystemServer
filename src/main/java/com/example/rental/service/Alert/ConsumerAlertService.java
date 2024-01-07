package com.example.rental.service.Alert;

import com.example.rental.utils.Result;

import java.util.List;
import java.util.Map;

public interface ConsumerAlertService {
    Result getAlertByConsumerId(String ConsumerId);

    Result getAlertByConsumerIdAndAlertId(String ConsumerId,String AlertId);

    Result updateConsumerAlertStatus(String ConsumerId,String AlertId);

    Result getAlertCountByConsumerId(String ConsumerId);
}
