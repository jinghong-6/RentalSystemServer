package com.example.rental.dao.Order;

import com.example.rental.domain.Order.OrderNopay;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderNopayDao {
    @Select("select * from order_nopay where uuid = #{uuid}")
    Map<String,Object> getNopayOrderByUuid(String uuid);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_end_time " +
            "from " +
            "order_nopay " +
            "where " +
            "consumer_id = #{consumer_id} " +
            "ORDER BY order_end_time ASC")
    List<Map<String,Object>> getOrderByConsumerId(String consumer_id);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_end_time " +
            "from " +
            "order_nopay " +
            "where " +
            "landlord_id = #{landlord_id} " +
            "ORDER BY order_end_time ASC")
    List<Map<String,Object>> getOrderByLandlordId(String landlord_id);

    @Select("select order_id,price_all from order_nopay where uuid = #{uuid}")
    Map<String,String> getOrderByUuid(String uuid);

    @Select("select " +
            "consumer_id,landlord_id,house_id,price_all,begin_time,end_time,order_end_time from order_nopay " +
            "where " +
            "uuid = #{uuid}")
    Map<String,Object> getPayOrderInfoByUuid(String uuid);

    @Select("select uuid from order_nopay where order_end_time < #{dateTime}")
    List<String> getAllProcessExpiredOrdersByDateTime(String dateTime);

    @Insert("insert into order_nopay " +
            "values(" +
            "#{id},#{uuid},#{order_id},#{consumer_id},#{landlord_id},#{house_id},#{price_all},#{landlord_money},#{admin_money}," +
            "#{price},#{begin_time},#{end_time},#{people_num},#{order_begin_time},#{order_end_time},#{order_status}" +
            ")")
    boolean addOrder(OrderNopay orderNopay);

    @Delete("DELETE FROM order_nopay WHERE uuid = #{uuid}")
    boolean deleteDataFromOrderNopay(String uuid);

    @Update("update order_nopay set order_status = '5' where uuid = #{uuid}")
    boolean updateOrderStatusToEnd(String uuid);

    @Select("SELECT uuid " +
            "FROM order_nopay " +
            "WHERE house_id = #{houseId} AND ( " +
            "    (#{new_begin_time} BETWEEN begin_time AND end_time) " +
            "    OR (#{new_end_time} BETWEEN begin_time AND end_time) " +
            "    OR (begin_time < #{new_begin_time} AND end_time > #{new_end_time}) " +
            "    OR (begin_time >= #{new_begin_time} AND end_time <= #{new_end_time}) " +
            ") " +
            "UNION " +
            "SELECT uuid " +
            "FROM order_begin " +
            "WHERE house_id = #{houseId} AND ( " +
            "    (#{new_begin_time} BETWEEN begin_time AND end_time) " +
            "    OR (#{new_end_time} BETWEEN begin_time AND end_time) " +
            "    OR (begin_time < #{new_begin_time} AND end_time > #{new_end_time}) " +
            "    OR (begin_time >= #{new_begin_time} AND end_time <= #{new_end_time}) " +
            ") " +
            "UNION " +
            "SELECT uuid " +
            "FROM order_complete " +
            "WHERE house_id = #{houseId} AND ( " +
            "    (#{new_begin_time} BETWEEN begin_time AND end_time) " +
            "    OR (#{new_end_time} BETWEEN begin_time AND end_time) " +
            "    OR (begin_time < #{new_begin_time} AND end_time > #{new_end_time}) " +
            "    OR (begin_time >= #{new_begin_time} AND end_time <= #{new_end_time}) " +
            ") " +
            "UNION " +
            "SELECT uuid " +
            "FROM order_completed " +
            "WHERE house_id = #{houseId} AND ( " +
            "    (#{new_begin_time} BETWEEN begin_time AND end_time) " +
            "    OR (#{new_end_time} BETWEEN begin_time AND end_time) " +
            "    OR (begin_time <= #{new_begin_time} AND end_time >= #{new_end_time}) " +
            "    OR (begin_time >= #{new_begin_time} AND end_time <= #{new_end_time}) " +
            ")")
    List<Map<String, String>> getConflictingOrders(@Param("houseId") String houseId, @Param("new_begin_time") String new_begin_time, @Param("new_end_time") String new_end_time);
}
