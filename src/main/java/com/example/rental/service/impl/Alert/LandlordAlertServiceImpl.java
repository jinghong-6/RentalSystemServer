package com.example.rental.service.impl.Alert;

import com.example.rental.dao.Alert.LandlordAlertDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompleteDao;
import com.example.rental.dao.Order.OrderNopayDao;
import com.example.rental.domain.Alert.LandlordAlert;
import com.example.rental.domain.House;
import com.example.rental.service.Alert.LandlordAlertService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public void addLandlordAlert(String type,String uuid){
        //商家新增订单通知
        if (type.equals("0")){
            Map<String, Object> Order = orderNopayDao.getNopayOrderByUuid(uuid);
            if (Order == null){
                Order = orderCompleteDao.getCompleteOrderByUuid(uuid);
            }

            String HouseId = Order.get("house_id").toString();
            String LandlordId = Order.get("landlord_id").toString();
            String OrderBeginTime = Order.get("begin_time").toString();
            String OrderEndTime = Order.get("end_time").toString();

            House house = houseDao.getHouseById(HouseId);

            LandlordAlert landlordAlert = new LandlordAlert();
            landlordAlert.setLandlord_id(LandlordId);
            landlordAlert.setAlert_status("0");
            landlordAlert.setTitle("新增订单通知");
            landlordAlert.setDatetime(getDateTime1());
            landlordAlert.setContent(
                    "您的" + house.getHouse_name() + "于" + getDateTime1() + "被预定了" +
                            ",预定日期为" + OrderBeginTime + "至" + OrderEndTime + ",快去确认吧。"
            );
            landlordAlertDao.InsertLandlordAlert(landlordAlert);
        }
    }

    private String getDateTime1() {
        // 创建 SimpleDateFormat 对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        return sdf.format(new Date());
    }

    private String getDateTime2() {
        // 创建 SimpleDateFormat 对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前时间
        return sdf.format(new Date());
    }
}
