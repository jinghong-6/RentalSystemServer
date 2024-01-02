package com.example.rental.service.impl.Order;

import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderEndDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.service.Order.OrderEndService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import java.text.ParseException;
import java.util.*;

@Service
public class OrderEndServiceImpl implements OrderEndService {
    @Autowired
    private OrderEndDao orderEndDao;

    @Autowired
    private HouseDao houseDao;

    @Autowired
    private LandlordDao landlordDao;

    @Override
    public Result getOrderByLandlordId(String landlord_id) {
        List<Map<String, Object>> Orders = orderEndDao.getOrderByLandlordId(landlord_id);
        List<Map<String, Object>> updatedOrders = processOrderDetails(Orders);

        if (!updatedOrders.isEmpty()) {
            return new Result(Code.SEARCH_OK, Orders);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关订单");
        }
    }

    @Override
    public Result updateOrderGrades(String grades, String uuid) {
        if (orderEndDao.updateOrderGrades(grades,uuid)){
            return new Result(Code.UPDATE_OK,"评价等级成功");
        }else {
            return new Result(Code.UPDATE_ERR,"评价等级失败");
        }

    }

    @Override
    public Result getNotRatedOrderByConsumerId(String ConsumerId) {
        List<Map<String, Object>> Orders = orderEndDao.getNotRatedCommentOrderByConsumerId(ConsumerId,"0");
        List<Map<String, Object>> updatedOrders = processOrderDetails(Orders);
        return new Result(Code.SEARCH_OK,updatedOrders);
    }

    @Override
    public Result getRatedOrderByConsumerId(String ConsumerId) {
        List<Map<String, Object>> Orders = orderEndDao.getRatedCommentOrderByConsumerId(ConsumerId);
        List<Map<String, Object>> updatedOrders = processOrderDetails(Orders);
        return new Result(Code.SEARCH_OK,updatedOrders);
    }

    @Override
    public Result getNotRatedCommentOrderByLandlordId(String LandlordId) {
        System.out.println(LandlordId);
        List<Map<String, Object>> Orders = orderEndDao.getNotRatedCommentOrderByLandlordId(LandlordId,"0");
        List<Map<String, Object>> updatedOrders = processOrderDetails(Orders);
        return new Result(Code.SEARCH_OK,updatedOrders);
    }

    @Override
    public Result getRatedCommentOrderByLandlordId(String LandlordId) {
        List<Map<String, Object>> Orders = orderEndDao.getRatedCommentOrderByLandlordId(LandlordId);
        List<Map<String, Object>> updatedOrders = processOrderDetails(Orders);
        return new Result(Code.SEARCH_OK,updatedOrders);
    }

    @Override
    public Result getOrderByConsumerId(String consumer_id) {
        List<Map<String, Object>> Orders = orderEndDao.getOrderByConsumerId(consumer_id);
        List<Map<String, Object>> updatedOrders = processOrderDetails(Orders);

        if (!updatedOrders.isEmpty()) {
            return new Result(Code.SEARCH_OK, updatedOrders);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关订单");
        }
    }

    private List<Map<String, Object>> processOrderDetails(List<Map<String, Object>> Orders) {
        List<Map<String, Object>> updatedOrders = new ArrayList<>();

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
                    String order_pay_time = order.get("order_pay_time") != null ? order.get("order_pay_time").toString() : "";
                    String order_confirm_time = order.get("order_confirm_time") != null ? order.get("order_confirm_time").toString() : "";
                    String order_close_time = order.get("order_close_time").toString();

                    Date date1;
                    Date date2;
                    Date date3;
                    Date date4;
                    try {
                        date1 = inputFormat.parse(order_begin_time);
                        date2 = order_pay_time.isEmpty() ? null : inputFormat.parse(order_pay_time);
                        date3 = order_confirm_time.isEmpty() ? null : inputFormat.parse(order_confirm_time);
                        date4 = inputFormat.parse(order_close_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue; // 处理日期解析异常
                    }

                    order.put("order_begin_time", outputFormat.format(date1));
                    order.put("order_pay_time", date2 != null ? outputFormat.format(date2) : "");
                    order.put("order_confirm_time", date3 != null ? outputFormat.format(date3) : "");
                    order.put("order_close_time", outputFormat.format(date4));
                }

                Map<String, String> landInfo = landlordDao.getLandlordById(landlordId);

                if (landInfo != null) {
                    order.put("landName", landInfo.get("landlord_name"));
                    order.put("landTele", landInfo.get("tele"));
                }

                updatedOrders.add(order);
            }
        }
        return updatedOrders;
    }
}