package com.example.rental.service.Alert;

import com.example.rental.utils.Result;

public interface LandlordAlertService {
    Result getAlertByLandlordId(String ConsumerId);

    Result getAlertByLandlordIdAndAlertId(String LandlordId,String AlertId);

    Result updateLandlordAlertStatus(String LandlordId,String AlertId);

    Result getAlertCountByLandlordId(String LandlordId);
}
