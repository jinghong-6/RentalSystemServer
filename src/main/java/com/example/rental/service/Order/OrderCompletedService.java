package com.example.rental.service.Order;

import com.example.rental.utils.Result;

public interface OrderCompletedService {
    //  查询消费者订单
    public Result getOrderByConsumerId(String consumer_id);

    //  查询商家订单
    public Result getOrderByLandlordId(String landlord_id);
}
