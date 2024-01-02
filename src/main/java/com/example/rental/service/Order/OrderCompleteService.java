package com.example.rental.service.Order;

import com.example.rental.utils.Result;

public interface OrderCompleteService {
    //  查询是否支付成功
    public Result getPaySuccessOrFailed(String uuid);

    //  查询消费者订单
    public Result getOrderByConsumerId(String consumer_id);

    //  查询商家订单
    public Result getOrderByLandlordId(String landlord_id);

    //  改变订单状态至待开始
    public Result addCompletedOrder(String uuid);
}
