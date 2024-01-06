package com.example.rental.service.impl.Alert;

import com.example.rental.dao.Alert.ConsumerAlertDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompleteDao;
import com.example.rental.dao.Order.OrderNopayDao;
import com.example.rental.domain.Alert.ConsumerAlert;
import com.example.rental.domain.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class ConsumerAlertServiceImpl {
    @Autowired
    ConsumerAlertDao consumerAlertDao;

    @Autowired
    OrderNopayDao orderNopayDao;

    @Autowired
    HouseDao houseDao;

    public void addConsumerAlert(String ConsumerId,Long OrderPrice,String HouseId,String OrderBeginTime,String OrderEndTime){
        House house =houseDao.getHouseById(HouseId);

        ConsumerAlert consumerAlert = new ConsumerAlert();
        consumerAlert.setConsumer_id(ConsumerId);
        consumerAlert.setAlert_status("0");
        consumerAlert.setTitle("扣款通知");
        consumerAlert.setDatetime(getDateTime1());
        consumerAlert.setContent(
                "您于" + getDateTime2() + "预定了" + house.getHouse_name() +
                ",预定日期为" + OrderBeginTime + "至" + OrderEndTime + ",共支出" + OrderPrice + "元。"
        );
        consumerAlertDao.InsertConsumerAlert(consumerAlert);
    }

    private String getDateTime1(){
        // 创建 SimpleDateFormat 对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        return sdf.format(new Date());
    }

    private String getDateTime2(){
        // 创建 SimpleDateFormat 对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前时间
        return sdf.format(new Date());
    }
}
