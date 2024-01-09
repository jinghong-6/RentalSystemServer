package com.example.rental.service.impl.Alert;

import com.example.rental.service.Alert.LandlordAlertService;
import com.example.rental.utils.Result;
import org.springframework.stereotype.Service;

@Service
public class LandlordAlertServiceImpl implements LandlordAlertService {
    @Override
    public Result getAlertByLandlordId(String LandlordId) {
        return null;
    }

    @Override
    public Result getAlertByLandlordIdAndAlertId(String LandlordId, String AlertId) {
        return null;
    }

    @Override
    public Result updateLandlordAlertStatus(String LandlordId, String AlertId) {
        return null;
    }

    @Override
    public Result getAlertCountByLandlordId(String LandlordId) {
        return null;
    }
}
