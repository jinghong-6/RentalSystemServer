package com.example.rental.service.Order;

import com.example.rental.domain.Order.OrderNopay;
import com.example.rental.utils.Result;

public interface OrderNopayService {
    //  查询消费者订单
    public Result getOrderByConsumerId(String consumer_id);

    //  查询商家订单
    public Result getOrderByLandlordId(String landlord_id);

    //  增加订单
    public Result addOrder(OrderNopay orderNopay);

    //  查询订单信息
    public Result getOrderByUuid(String uuid);

    //  支付订单
    public Result payOrderByPwd(String uuid,String pwd);
}
