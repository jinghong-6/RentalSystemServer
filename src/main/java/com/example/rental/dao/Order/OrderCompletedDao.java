package com.example.rental.dao.Order;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderCompletedDao {
    @Select("select * from order_completed where uuid = #{uuid}")
    Map<String, Object> getCompletedOrderByUuid(String uuid);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_pay_time,order_confirm_time " +
            "from " +
            "order_completed " +
            "where " +
            "consumer_id = #{consumer_id}")
    public List<Map<String, Object>> getOrderByConsumerId(String consumer_id);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_pay_time,order_confirm_time " +
            "from " +
            "order_completed " +
            "where " +
            "landlord_id = #{landlord_id}")
    public List<Map<String, Object>> getOrderByLandlordId(String landlord_id);

    @Select("select uuid from order_completed where begin_time <= #{date}")
    public List<String> getAllProcessExpiredOrdersByDate(String date);


    @Insert("INSERT INTO " +
            "order_completed " +
            "(uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time, order_pay_time) " +
            "SELECT " +
            "uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time, order_pay_time " +
            "FROM " +
            "order_complete " +
            "WHERE uuid = #{uuid}")
    boolean moveOrderCompleteToOrderCompleted(String uuid);

    @Delete("DELETE FROM order_completed WHERE uuid = #{uuid}")
    public boolean deleteDataFromOrderCompleted(String uuid);

    @Update("update order_completed set order_confirm_time = #{orderConfirmTime} where uuid = #{uuid}")
    public boolean updateOrderConfirmTime(@Param("orderConfirmTime") String orderConfirmTime, @Param("uuid") String uuid);
}
