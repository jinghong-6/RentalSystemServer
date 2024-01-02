package com.example.rental.service.impl.Order;

import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompletedDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.service.Order.OrderCompletedService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderCompletedServiceImpl implements OrderCompletedService {
    @Autowired
    private OrderCompletedDao orderCompletedDao;

    @Autowired
    private HouseDao houseDao;

    @Autowired
    private LandlordDao landlordDao;

    @Override
    public Result getOrderByConsumerId(String consumer_id) {
        List<Map<String, Object>> Orders = orderCompletedDao.getOrderByConsumerId(consumer_id);
        boolean ordersFound = processOrderDetails(Orders);

        if (ordersFound) {
            return new Result(Code.SEARCH_OK, Orders);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关订单");
        }
    }

    @Override
    public Result getOrderByLandlordId(String landlord_id) {
        List<Map<String, Object>> Orders = orderCompletedDao.getOrderByLandlordId(landlord_id);
        boolean ordersFound = processOrderDetails(Orders);

        if (ordersFound) {
            return new Result(Code.SEARCH_OK, Orders);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关订单");
        }
    }

    private boolean processOrderDetails(List<Map<String, Object>> Orders) {
        if (Orders != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Map<String, Object> order : Orders) {
                String houseId = String.valueOf(order.get("house_id"));
                String landlordId = String.valueOf(order.get("landlord_id"));

                Map<String, Object> houseInfo = houseDao.getHouseNameAndImgById(houseId);
                if (houseInfo != null) {
                    order.put("house_name", houseInfo.get("house_name"));
                    order.put("house_img", houseInfo.get("firstImg"));

                    String order_begin_time = order.get("order_begin_time").toString();
                    String order_pay_time = order.get("order_pay_time").toString();
                    String order_confirm_time = order.get("order_confirm_time").toString();

                    Date date1;
                    Date date2;
                    Date date3;
                    try {
                        date1 = inputFormat.parse(order_begin_time);
                        date2 = inputFormat.parse(order_pay_time);
                        date3 = inputFormat.parse(order_confirm_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue; // 处理日期解析异常
                    }

                    order.put("order_begin_time", outputFormat.format(date1));
                    order.put("order_pay_time", outputFormat.format(date2));
                    order.put("order_confirm_time",outputFormat.format(date3));

                }
                Map<String, String> landInfo = landlordDao.getLandlordById(landlordId);
                if (landInfo != null) {
                    order.put("landName",landInfo.get("landlord_name"));
                    order.put("landTele",landInfo.get("tele"));
                }
            }
            return !Orders.isEmpty(); // 返回 true 表示找到相关订单
        } else {
            return false; // 返回 false 表示未找到相关订单
        }
    }


}
