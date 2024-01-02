package com.example.rental.service.Order;

import com.example.rental.utils.Result;
import org.apache.ibatis.annotations.Param;

public interface OrderEndService {
    //  获取当前用户未评价的订单
    Result getNotRatedOrderByConsumerId(String ConsumerId);

    //  获取当前用户已评价的订单
    Result getRatedOrderByConsumerId(String ConsumerId);

    //  获取当前房东未评价的订单
    Result getNotRatedCommentOrderByLandlordId(String LandlordId);

    //  获取当前房东已评价的订单
    Result getRatedCommentOrderByLandlordId(String LandlordId);

    //  查询消费者订单
    public Result getOrderByConsumerId(String consumer_id);

    //  查询商家订单
    public Result getOrderByLandlordId(String landlord_id);

    //  设置评价等级
    public Result  updateOrderGrades(String grades,String uuid);
}
