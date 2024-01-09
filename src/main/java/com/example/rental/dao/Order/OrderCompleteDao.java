package com.example.rental.dao.Order;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderCompleteDao {
    @Select("select * from order_complete where uuid = #{uuid}")
    Map<String,Object> getCompleteOrderByUuid(String uuid);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_end_time,order_pay_time " +
            "from " +
            "order_complete " +
            "where " +
            "consumer_id = #{consumer_id}")
    public List<Map<String, Object>> getOrderByConsumerId(String consumer_id);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_end_time,order_pay_time " +
            "from " +
            "order_complete " +
            "where " +
            "landlord_id = #{landlord_id}")
    public List<Map<String, Object>> getOrderByLandlordId(String landlord_id);

    @Select("select uuid from order_complete where uuid = #{uuid}")
    public List<Map<String, String>> getPaySuccessOrFailed(String uuid);

    @Insert("INSERT INTO " +
            "order_complete " +
            "(uuid, order_id, consumer_id, landlord_id, house_id, price_all, price, " +
            "begin_time, end_time, people_num, order_begin_time) " +
            "SELECT " +
            "uuid, order_id, consumer_id, landlord_id, house_id, price_all, price, " +
            "begin_time, end_time, people_num, order_begin_time " +
            "FROM " +
            "order_nopay " +
            "WHERE uuid = #{uuid}")
    boolean moveDataToOrderComplete(String uuid);

    @Delete("DELETE FROM order_complete WHERE uuid = #{uuid}")
    public boolean deleteDataFromOrderComplete(String uuid);

    @Update("update order_complete set order_status = '4' where uuid = #{uuid}")
    public boolean updateOrderStatusToEnd(String uuid);

    @Update("update order_complete set order_pay_time = #{orderPayTime},order_end_time = #{orderEndTime} where uuid = #{uuid}")
    public boolean updateOrderPayTimeAndEndTime(@Param("orderPayTime") String orderPayTime, @Param("orderEndTime") String orderEndTime, @Param("uuid") String uuid);

    @Select("select uuid from order_complete where order_end_time < #{dateTime}")
    public List<String> getAllProcessExpiredOrdersByDateTime(String dateTime);
}