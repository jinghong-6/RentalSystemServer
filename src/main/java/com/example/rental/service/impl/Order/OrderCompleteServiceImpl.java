package com.example.rental.service.impl.Order;

import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompleteDao;
import com.example.rental.dao.Order.OrderCompletedDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.service.Order.OrderCompleteService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderCompleteServiceImpl implements OrderCompleteService {
    @Autowired
    private OrderCompleteDao orderCompleteDao;

    @Autowired
    private HouseDao houseDao;

    @Autowired
    private LandlordDao landlordDao;

    @Autowired
    private OrderRollbackService orderRollbackService;

    @Override
    public Result getPaySuccessOrFailed(String uuid) {
        List<Map<String,String>> paySuccessOrFailed = orderCompleteDao.getPaySuccessOrFailed(uuid);
        if (paySuccessOrFailed.isEmpty()){
            return new Result(Code.SEARCH_ERR,"支付失败");
        }else if (paySuccessOrFailed.size() == 1){
            return new Result(Code.SEARCH_OK,"支付成功");
        }else {
            return new Result(Code.SEARCH_ERR,"支付异常，请联系管理员");
        }
    }

    @Override
    public Result getOrderByConsumerId(String consumer_id) {
        List<Map<String, Object>> Orders = orderCompleteDao.getOrderByConsumerId(consumer_id);
        boolean ordersFound = processOrderDetails(Orders);

        if (ordersFound) {
            return new Result(Code.SEARCH_OK, Orders);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关订单");
        }
    }

    @Override
    public Result getOrderByLandlordId(String landlord_id) {
        List<Map<String, Object>> Orders = orderCompleteDao.getOrderByLandlordId(landlord_id);
        boolean ordersFound = processOrderDetails(Orders);

        if (ordersFound) {
            return new Result(Code.SEARCH_OK, Orders);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关订单");
        }
    }

    /**
     * 格式化订单详情信息，包括获取房屋信息、格式化日期、获取房东信息等。
     *
     * @param Orders 包含订单信息的列表(List<Map<String, Object>>)
     * @return 如果成功处理订单详情，则返回 true，表示找到相关订单；否则返回 false，表示未找到相关订单或处理过程中发生异常。
     */
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
                    String order_end_time = order.get("order_end_time").toString();
                    String order_pay_time = order.get("order_pay_time").toString();

                    Date date1;
                    Date date2;
                    Date date3;
                    try {
                        date1 = inputFormat.parse(order_begin_time);
                        date2 = inputFormat.parse(order_end_time);
                        date3 = inputFormat.parse(order_pay_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue; // 处理日期解析异常
                    }

                    order.put("order_begin_time", outputFormat.format(date1));
                    order.put("order_end_time", outputFormat.format(date2));
                    order.put("order_pay_time",outputFormat.format(date3));
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

    @Override
    public Result addCompletedOrder(String uuid) {
        return orderRollbackService.moveCompleteToCompletedFromOrderComplete(uuid);
    }
}
