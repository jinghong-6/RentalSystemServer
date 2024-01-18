package com.example.rental.service.impl.Order;

import com.alibaba.fastjson.JSONObject;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompleteDao;
import com.example.rental.dao.Order.OrderNopayDao;
import com.example.rental.dao.Role.ConsumerDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.domain.Order.OrderNopay;
import com.example.rental.service.Order.OrderNopayService;
import com.example.rental.service.impl.Alert.ConsumerAlertServiceImpl;
import com.example.rental.service.impl.Alert.LandlordAlertServiceImpl;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderNopayServiceImpl implements OrderNopayService {
    private static final int MAX_PWD_LOCK_ATTEMPTS = 5;

    @Autowired
    private OrderNopayDao orderNopayDao;

    @Autowired
    private ConsumerDao consumerDao;

    @Autowired
    private checkAndProcessExpiredOrders checkAndProcessExpiredOrder;

    @Autowired
    private HouseDao houseDao;

    @Autowired
    private LandlordDao landlordDao;

    @Autowired
    private OrderRollbackService orderRollbackService;

    @Autowired
    ConsumerAlertServiceImpl consumerAlertService;

    @Autowired
    LandlordAlertServiceImpl landlordAlertService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "itcast.topic", type = ExchangeTypes.TOPIC),
            key = "order.#"
    ))
    public void listenQueue1(String orderNopay) {
        //  接收要反序列化
        OrderNopay orderNopay1 = JSONObject.parseObject(orderNopay, OrderNopay.class);
        addOrder(orderNopay1);
    }

    @Override
    public Result addOrder(OrderNopay orderNopay) {
        if (orderNopayDao.addOrder(orderNopay)) {
            System.out.println("订单生成成功");
            landlordAlertService.addLandlordAlert("0",orderNopay.getUuid());
            return new Result(Code.SAVE_OK, "订单生成成功");
        } else {
            return new Result(Code.SAVE_ERR, "订单生成失败");
        }

    }

    @Override
    public Result getOrderByConsumerId(String consumer_id) {
        List<Map<String, Object>> Orders = orderNopayDao.getOrderByConsumerId(consumer_id);
        boolean ordersFound = processOrderDetails(Orders);

        if (ordersFound) {
            return new Result(Code.SEARCH_OK, Orders);
        } else {
            return new Result(Code.SEARCH_ERR, "未找到相关订单");
        }
    }

    @Override
    public Result getOrderByLandlordId(String landlord_id) {
        List<Map<String, Object>> Orders = orderNopayDao.getOrderByLandlordId(landlord_id);
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
                    String order_end_time = order.get("order_end_time").toString();

                    Date date1;
                    Date date2;
                    try {
                        date1 = inputFormat.parse(order_begin_time);
                        date2 = inputFormat.parse(order_end_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue; // 处理日期解析异常
                    }

                    order.put("order_begin_time", outputFormat.format(date1));
                    order.put("order_end_time", outputFormat.format(date2));
                }

                Map<String, String> landInfo = landlordDao.getLandlordById(landlordId);
                if (landInfo != null) {
                    order.put("landName", landInfo.get("landlord_name"));
                    order.put("landTele", landInfo.get("tele"));
                }
            }
            return !Orders.isEmpty(); // 返回 true 表示找到相关订单
        } else {
            return false; // 返回 false 表示未找到相关订单
        }
    }

    @Override
    public Result getOrderByUuid(String uuid) {
        Map<String, String> Order = orderNopayDao.getOrderByUuid(uuid);
        if (Order != null) {
            return new Result(Code.SEARCH_OK, Order);
        } else {
            return new Result(Code.SEARCH_ERR, "订单查询失败");
        }
    }

    /**
     * 根据UUID和支付密码进行订单支付。
     *
     * @param uuid 订单的唯一标识符
     * @param pwd  支付密码
     * @return 表示支付结果的 Result 对象
     * @throws IllegalArgumentException 如果输入参数无效
     * @throws RuntimeException         如果支付操作失败，触发回滚
     * @example Result result = payOrderByPwd("your_uuid_here", "your_password_here");
     * if (result.isSuccess()) {
     * // 支付成功
     * } else {
     * // 支付失败，处理错误信息
     * }
     */
    @Override
    public Result payOrderByPwd(String uuid, String pwd) {
        // 通过UUID获取订单信息
        Map<String, Object> orderNoPay = orderNopayDao.getPayOrderInfoByUuid(uuid);

        // 检查订单是否存在
        if (orderNoPay == null) {
            return new Result(Code.SEARCH_ERR, "订单不存在，请勿重复提交");
        }

        String consumerId = String.valueOf(orderNoPay.get("consumer_id"));
        LocalDateTime orderEndTime = (LocalDateTime) orderNoPay.get("order_end_time");

        // 检查订单是否已过期
        if (isOrderExpired(orderEndTime)) {
            System.out.println("订单已超时。");
            checkAndProcessExpiredOrder.checkAndProcessExpiredOrders();
            return new Result(Code.SAVE_ERR, "订单已超时");
        }

        // 检查消费者状态
        int consumerStatus = consumerDao.getConsumerStatus(consumerId);
        if (consumerStatus != 0) {
            return new Result(Code.SEARCH_ERR, "账号异常，请联系管理员");
        }

        int pwdLockNum = consumerDao.getPwdLockNumById(consumerId);
        // 检查消费者账号是否已被锁定
        if (pwdLockNum >= MAX_PWD_LOCK_ATTEMPTS) {
            consumerDao.LockConsumer(consumerId);
            return new Result(Code.SAVE_ERR, "失败次数过多，已封禁账号");
        }
        // 验证支付密码
        if (!pwd.equals(consumerDao.getPayPwdById(consumerId))) {
            System.out.println("id为" + orderNoPay.get("consumer_id") + "的密码输入错误");
            consumerDao.updatePayPwdLockNum(consumerId);
            return new Result(Code.SEARCH_ERR, "密码错误");
        }
        //  验证余额
        BigDecimal orderPrice = new BigDecimal(String.valueOf(orderNoPay.get("price_all")));
        BigDecimal consumerMoney = new BigDecimal(consumerDao.getMoneyById(consumerId));
        if (consumerMoney.compareTo(orderPrice) < 0) {
            return new Result(Code.SAVE_ERR, "余额不足");
        }

        BigDecimal newMoney = consumerMoney.subtract(orderPrice);

        //  房东id
        String landlordId = String.valueOf(orderNoPay.get("landlord_id"));
        Result result = orderRollbackService.moveDataToOrderCompleteAndDeleteDataFromOrderNopay(uuid,consumerId,landlordId,newMoney,orderPrice);
        if (result.getCode().toString().equals("901")){
            // 操作成功
            consumerAlertService.addConsumerAlert("0",uuid);
        }
        return result;
    }

    @Override
    public Result CancelNopayOrder(String uuid) {
        if (orderRollbackService.moveOrderNopayToOrderEndAndFromOrderNopay(uuid,"1")){
            return new Result(Code.UPDATE_OK,"取消成功");
        }else {
            return new Result(Code.UPDATE_ERR,"取消失败");
        }

    }

    private boolean isOrderExpired(LocalDateTime orderEndTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isAfter(orderEndTime);
    }
}
