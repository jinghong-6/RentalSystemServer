package com.example.rental.dao.Order;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderEndDao {
    @Select("select * from order_end where uuid = #{uuid}")
    Map<String, Object> getEndOrderByUuid(String uuid);

    @Select("select uuid,grades from order_end where house_id = #{houseId} order by order_close_time desc")
    List<Map<String, String>> getUuidByHouseId(String houseId);

    @Select("select house_id from order_end where consumer_id = #{consumer_id}")
    List<String> getHouseIdByConsumerId(String consumerId);

    @Select("select house_id from order_end where landlord_id = #{landlord_id}")
    List<String> getHouseIdByLandLordId(String landlord_id);

    @Select("SELECT " +
            "COUNT(CASE WHEN order_status = '0' THEN 1 END) AS OrderEndNum, " +
            "COUNT(CASE WHEN order_status != '0' THEN 1 END) AS OrderOtherNum " +
            "FROM order_end where consumer_id = #{consumer_id}")
    Map<String, String> getOrderEndNumByConsumerId(String consumer_id);

    @Select("SELECT " +
            "COUNT(CASE WHEN order_status = '0' THEN 1 END) AS OrderEndNum, " +
            "COUNT(CASE WHEN order_status != '0' THEN 1 END) AS OrderOtherNum " +
            "FROM order_end where landlord_id = #{landlord_id}")
    Map<String, String> getOrderEndNumByLandLordId(String consumer_id);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num," +
            "order_begin_time,order_pay_time,order_confirm_time,order_close_time,order_status " +
            "from " +
            "order_end " +
            "where " +
            "consumer_id = #{ConsumerId} and grades = #{grades} " +
            "ORDER BY order_close_time DESC")
    List<Map<String, Object>> getNotRatedCommentOrderByConsumerId(@Param("ConsumerId") String ConsumerId, @Param("grades") String grades);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num," +
            "order_begin_time,order_pay_time,order_confirm_time,order_close_time,order_status,grades " +
            "from " +
            "order_end " +
            "where " +
            "consumer_id = #{ConsumerId} and grades != '0' " +
            "ORDER BY order_close_time DESC")
    List<Map<String, Object>> getRatedCommentOrderByConsumerId(String ConsumerId);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num," +
            "order_begin_time,order_pay_time,order_confirm_time,order_close_time,order_status " +
            "from " +
            "order_end " +
            "where " +
            "landlord_id = #{LandlordId} and grades = #{grades} " +
            "ORDER BY order_close_time DESC")
    List<Map<String, Object>> getNotRatedCommentOrderByLandlordId(@Param("LandlordId") String LandlordId, @Param("grades") String grades);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num," +
            "order_begin_time,order_pay_time,order_confirm_time,order_close_time,order_status,grades " +
            "from " +
            "order_end " +
            "where " +
            "landlord_id = #{LandlordId} and grades != '0'" +
            "ORDER BY order_close_time DESC")
    List<Map<String, Object>> getRatedCommentOrderByLandlordId(String LandlordId);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num," +
            "order_begin_time,order_pay_time,order_confirm_time,order_close_time,order_status " +
            "from " +
            "order_end " +
            "where " +
            "consumer_id = #{consumer_id} " +
            "ORDER BY order_close_time DESC")
    List<Map<String, Object>> getOrderByConsumerId(String consumer_id);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num," +
            "order_begin_time,order_pay_time,order_confirm_time,order_close_time,order_status " +
            "from " +
            "order_end " +
            "where " +
            "landlord_id = #{landlord_id} " +
            "ORDER BY order_close_time DESC")
    List<Map<String, Object>> getOrderByLandlordId(String landlord_id);

    @Select("SELECT DATE_FORMAT(begin_time, '%Y-%m') AS month, COUNT(*) AS count,SUM(CAST(price_all AS SIGNED)) AS total_price " +
            "FROM ( " +
            "    SELECT begin_time,price_all FROM order_begin where consumer_id = #{ConsumerId} " +
            "    UNION ALL " +
            "    SELECT begin_time,price_all FROM order_complete where consumer_id = #{ConsumerId} " +
            "    UNION ALL " +
            "    SELECT begin_time,price_all FROM order_completed where consumer_id = #{ConsumerId} " +
            "    UNION ALL " +
            "    SELECT begin_time,price_all FROM order_nopay where consumer_id = #{ConsumerId} " +
            "    UNION ALL " +
            "    SELECT begin_time,price_all FROM order_end where consumer_id = #{ConsumerId} " +
            ") AS combined_tables " +
            "GROUP BY month;")
    List<Map<String, String>> getOrderDateCountByConsumerId(String ConsumerId);

    @Select("SELECT DATE_FORMAT(begin_time, '%Y-%m') AS month, COUNT(*) AS count,SUM(CAST(landlord_money AS DECIMAL(10,2))) AS total_price " +
            "FROM ( " +
            "    SELECT begin_time,CAST(landlord_money AS DECIMAL(10,2)) AS landlord_money FROM order_begin where landlord_id = #{landlord_id} " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(landlord_money AS DECIMAL(10,2)) AS landlord_money FROM order_complete where landlord_id = #{landlord_id} " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(landlord_money AS DECIMAL(10,2)) AS landlord_money FROM order_completed where landlord_id = #{landlord_id} " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(landlord_money AS DECIMAL(10,2)) AS landlord_money FROM order_nopay where landlord_id = #{landlord_id} " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(landlord_money AS DECIMAL(10,2)) AS landlord_money FROM order_end where landlord_id = #{landlord_id} " +
            ") AS combined_tables " +
            "GROUP BY month;")
    List<Map<String, String>> getOrderDateCountByLandlordId(String landlord_id);

    @Insert("INSERT INTO " +
            "order_end " +
            "(uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time,order_status) " +
            "SELECT " +
            "uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time,order_status " +
            "FROM " +
            "order_nopay " +
            "WHERE uuid = #{uuid}")
    boolean moveOrderNopayToOrderEnd(String uuid);

    @Insert("INSERT INTO " +
            "order_end " +
            "(uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time,order_pay_time,order_close_time,order_status) " +
            "SELECT " +
            "uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time,order_pay_time,order_end_time,order_status " +
            "FROM " +
            "order_complete " +
            "WHERE uuid = #{uuid}")
    boolean moveCompleteToOrderEnd(String uuid);

    @Insert("INSERT INTO " +
            "order_end " +
            "(uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time,order_pay_time,order_confirm_time) " +
            "SELECT " +
            "uuid, order_id, consumer_id, landlord_id, house_id, price_all,landlord_money,admin_money, price, " +
            "begin_time, end_time, people_num, order_begin_time,order_pay_time,order_confirm_time " +
            "FROM " +
            "order_begin " +
            "WHERE uuid = #{uuid}")
    boolean moveBeginToOrderEnd(String uuid);

    @Update("update order_end set order_close_time = #{orderCloseTime} where uuid = #{uuid}")
    public boolean updateOrderCloseTime(@Param("orderCloseTime") String orderCloseTime, @Param("uuid") String uuid);

    @Update("update order_end set grades = #{grades} where uuid = #{uuid}")
    public boolean updateOrderGrades(@Param("grades") String grades, @Param("uuid") String uuid);
}
