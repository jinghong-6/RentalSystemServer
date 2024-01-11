package com.example.rental.service.impl.Alert;

import com.example.rental.dao.Alert.LandlordAlertDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompleteDao;
import com.example.rental.dao.Order.OrderCompletedDao;
import com.example.rental.dao.Order.OrderNopayDao;
import com.example.rental.service.Alert.LandlordAlertService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LandlordAlertServiceImpl implements LandlordAlertService {
    @Autowired
    LandlordAlertDao landlordAlertDao;

    @Autowired
    OrderNopayDao orderNopayDao;

    @Autowired
    HouseDao houseDao;

    @Autowired
    OrderCompleteDao orderCompleteDao;

    @Autowired
    OrderCompletedDao orderCompletedDao;

    @Override
    public Result getAlertByLandlordId(String LandlordId) {
        List<Map<String, String>> AlertList = landlordAlertDao.getAlertListByLandlordId(LandlordId);
        if (AlertList.size() == 0) {
            return new Result(Code.SEARCH_ERR, "暂无通知");
        } else {
            return new Result(Code.SEARCH_OK, AlertList);
        }
    }

    @Override
    public Result getAlertByLandlordIdAndAlertId(String LandlordId, String AlertId) {
        Map<String, String> Alert = landlordAlertDao.getAlertByLandlordIdAndAlertId(LandlordId, AlertId);
        if (Alert != null) {
            return new Result(Code.SEARCH_OK, Alert);
        } else {
            return new Result(Code.SEARCH_ERR, "未查询到");
        }
    }

    @Override
    public Result updateLandlordAlertStatus(String LandlordId, String AlertId) {
        if (landlordAlertDao.updateLandlordAlertStatus(LandlordId, AlertId)) {
            return new Result(Code.UPDATE_OK, "已读");
        } else {
            return new Result(Code.UPDATE_ERR, "已读失败");
        }
    }

    @Override
    public Result getAlertCountByLandlordId(String LandlordId) {
        Integer num = landlordAlertDao.getAlertCountByLandlordId(LandlordId);
        if (num != null) {
            return new Result(Code.SEARCH_OK, num);
        } else {
            return new Result(Code.SEARCH_ERR, "查询失败");
        }
    }
}
