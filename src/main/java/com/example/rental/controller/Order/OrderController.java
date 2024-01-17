package com.example.rental.controller.Order;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderNopayDao;
import com.example.rental.domain.House;
import com.example.rental.domain.Order.OrderNopay;
import com.example.rental.service.Order.OrderBeginService;
import com.example.rental.service.Order.OrderCompleteService;
import com.example.rental.service.Order.OrderCompletedService;
import com.example.rental.service.Order.OrderEndService;
import com.example.rental.service.Order.OrderNopayService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/Order")
public class OrderController {
    @Autowired
    private OrderBeginService orderBeginService;

    @Autowired
    private OrderCompleteService orderCompleteService;

    @Autowired
    private OrderCompletedService orderCompletedService;

    @Autowired
    private OrderEndService orderEndService;

    @Autowired
    private OrderNopayService orderNopayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HouseDao houseDao;

    @Autowired
    private OrderNopayDao orderNopayDao;

    @PostMapping("/getPayOutcome")
    public Result getPaySuccessOrFailed(String uuid){
        return orderCompleteService.getPaySuccessOrFailed(uuid);
    }

    @PostMapping("/addOrder")
    public Result addOrder(OrderNopay orderNopay) {
        House orderHouse = houseDao.getHouseById(orderNopay.getHouse_id());

        try {
            String OrderStartStr = orderNopay.getBegin_time();
            String OrderEndStr = orderNopay.getEnd_time();
            String HouseStartStr = orderHouse.getBegin_time();
            String HouseEndStr = orderHouse.getEnd_time();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date HouseStartDate = dateFormat.parse(HouseStartStr);
            Date HouseEndDate = dateFormat.parse(HouseEndStr);
            Date OrderStartDate = dateFormat.parse(OrderStartStr);
            Date OrderEndDate = dateFormat.parse(OrderEndStr);

            if (isRange2InsideRange1(HouseStartDate, HouseEndDate, OrderStartDate, OrderEndDate)) {

                //  获得订单支付截止和开始时间
                LocalDateTime currentDateTime = LocalDateTime.now(); // 获取当前时间
                LocalDateTime newDateTime = currentDateTime.plusMinutes(15); // 增加15分钟

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                String Order_begin_time = currentDateTime.format(formatter);
//                String Order_end_time = newDateTime.format(formatter);

                //  计算天数
                long dayNum = ChronoUnit.DAYS.between(LocalDate.parse(OrderStartStr), LocalDate.parse(OrderEndStr)) + 1;
                String AllPrice = String.valueOf(Long.parseLong(orderHouse.getPrice()) * dayNum);
                orderNopay.setUuid(getUUID());
                orderNopay.setPrice((orderHouse.getPrice()));
                orderNopay.setPrice_all(AllPrice);
                // 将 String 直接转换为 double
                BigDecimal allPriceBigDecimal = new BigDecimal(AllPrice);
                BigDecimal adminMoney = allPriceBigDecimal.multiply(new BigDecimal("0.01"));
                orderNopay.setAdmin_money(String.valueOf(adminMoney));
                orderNopay.setLandlord_money(String.valueOf(allPriceBigDecimal.subtract(adminMoney)));
                orderNopay.setOrder_id(generateOrderNumber());
                orderNopay.setOrder_begin_time(currentDateTime);
                orderNopay.setOrder_end_time(newDateTime);

                String houseId = orderNopay.getHouse_id();
                String new_begin_time = orderNopay.getBegin_time();
                String new_end_time = orderNopay.getEnd_time();

                //  验证所订日期是否可选
                if(orderNopayDao.getConflictingOrders(houseId,new_begin_time,new_end_time).isEmpty()){
                    //  发送到rabbitmq要先序列化
                    String orderNopayJsonObject = JSONObject.toJSONString(orderNopay);
                    rabbitTemplate.convertAndSend("itcast.topic", "order.saomiao", orderNopayJsonObject);

                    Map<String, String> returnInfo = new HashMap<>();
                    returnInfo.put("uuid",orderNopay.getUuid());
                    returnInfo.put("OrderEndTime", orderNopay.getOrder_end_time().format(formatter));

                    return new Result(Code.SAVE_OK, returnInfo);
                }else {
                    return new Result(Code.SAVE_ERR, "日期已被预定");
                }
            } else {
                return new Result(Code.SAVE_ERR, "订单生成失败,日期范围2不在日期范围1内");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return new Result(Code.SAVE_ERR, "订单生成失败");
        }
    }

    //  生成每个订单的uuid
    public String getUUID() {
        UUID uniqueRandomValue = UUID.randomUUID();
        return uniqueRandomValue.toString();
    }

    //  判断订单日期是否在可出售范围内
    public static boolean isRange2InsideRange1(Date range1Start, Date range1End, Date range2Start, Date range2End) {
        return range2Start.compareTo(range1Start) >= 0 && range2End.compareTo(range1End) <= 0;
    }

    //  生成订单编号
    public static String generateOrderNumber() {
        // 生成日期部分
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());

        // 生成随机数部分
        Random random = new Random();
        int randomNumber = random.nextInt(99999999); // 生成0到99999999之间的随机数
        String randomPart = String.format("%08d", randomNumber); // 格式化为八位数字字符串

        // 拼接日期和随机数部分
        String orderNumber = datePart + randomPart;

        return orderNumber;
    }

    @PostMapping("/getOrder")
    public Result getOrderByUuid(String uuid) {
        return orderNopayService.getOrderByUuid(uuid);
    }

    @PostMapping("/payOrder")
    public Result payOrderByPwd(String uuid,String pwd){
        return orderNopayService.payOrderByPwd(uuid,pwd);
    }

    @PostMapping("/getBeginOrderC")
    public Result getBeginOrderByConsumerId(String consumer_id){
        return orderBeginService.getOrderByConsumerId(consumer_id);
    }

    @PostMapping("/getCompleteOrderC")
    public Result getCompleteOrderByConsumerId(String consumer_id){
        return orderCompleteService.getOrderByConsumerId(consumer_id);
    }

    @PostMapping("/getCompletedOrderC")
    public Result getCompletedOrderByConsumerId(String consumer_id){
        return orderCompletedService.getOrderByConsumerId(consumer_id);
    }

    @PostMapping("/getEndOrderC")
    public Result getEndOrderByConsumerId(String consumer_id){
        return orderEndService.getOrderByConsumerId(consumer_id);
    }

    @PostMapping("/getNoPayOrderC")
    public Result getNoPayOrderByConsumerId(String consumer_id){
        return orderNopayService.getOrderByConsumerId(consumer_id);
    }

    @PostMapping("/getBeginOrderL")
    public Result getBeginOrderByLandlordId(String landlord_id){
        return orderBeginService.getOrderByLandlordId(landlord_id);
    }

    @PostMapping("/getCompleteOrderL")
    public Result getCompleteOrderByLandlordId(String landlord_id){
        return orderCompleteService.getOrderByLandlordId(landlord_id);
    }

    @PostMapping("/getCompletedOrderL")
    public Result getCompletedOrderByLandlordId(String landlord_id){
        return orderCompletedService.getOrderByLandlordId(landlord_id);
    }

    @PostMapping("/getEndOrderL")
    public Result getEndOrderByLandlordId(String landlord_id){
        return orderEndService.getOrderByLandlordId(landlord_id);
    }

    @PostMapping("/getNoPayOrderL")
    public Result getNoPayOrderByLandlordId(String landlord_id){
        return orderNopayService.getOrderByLandlordId(landlord_id);
    }

    @PostMapping("/checkOrder")
    public Result checkOrder(String uuid){
        return orderCompleteService.addCompletedOrder(uuid);
    }

    @PostMapping("/CancelOrder")
    public Result CancelOrder(String uuid){
        return orderCompleteService.addEndOrder(uuid);
    }

    @PostMapping("/CNotRated")
    public Result getNotRatedOrderByConsumerId(String ConsumerId){
        return orderEndService.getNotRatedOrderByConsumerId(ConsumerId);
    }

    @PostMapping("/CRated")
    public Result getRatedOrderByConsumerId(String ConsumerId){
        return orderEndService.getRatedOrderByConsumerId(ConsumerId);
    }

    @PostMapping("/LNotRated")
    public Result getNotRatedCommentOrderByLandlordId(String LandlordId){
        return orderEndService.getNotRatedCommentOrderByLandlordId(LandlordId);
    }

    @PostMapping("/LRated")
    public Result getRatedCommentOrderByLandlordId(String LandlordId){
        return orderEndService.getRatedCommentOrderByLandlordId(LandlordId);
    }

    @PostMapping("/setGrades")
    public Result updateOrderGrades(String grades, String uuid) {
        return orderEndService.updateOrderGrades(grades, uuid);
    }
}
