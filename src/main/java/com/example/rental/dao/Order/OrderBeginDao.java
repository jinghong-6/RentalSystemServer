package com.example.rental.dao.Order;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderBeginDao {
    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_pay_time,order_confirm_time " +
            "from " +
            "order_begin " +
            "where " +
            "consumer_id = #{consumer_id}")
    public List<Map<String,Object>> getOrderByConsumerId(String consumer_id);

    @Select("select " +
            "uuid,house_id,landlord_id,price_all,begin_time,end_time,people_num,order_begin_time,order_pay_time,order_confirm_time " +
            "from " +
            "order_begin " +
            "where " +
            "landlord_id = #{landlord_id}")
    public List<Map<String,Object>> getOrderByLandlordId(String landlord_id);

    @Insert("INSERT INTO " +
            "order_begin " +
            "(uuid, order_id, consumer_id, landlord_id, house_id, price_all, price, " +
            "begin_time, end_time, people_num, order_begin_time, order_pay_time,order_confirm_time) " +
            "SELECT " +
            "uuid, order_id, consumer_id, landlord_id, house_id, price_all, price, " +
            "begin_time, end_time, people_num, order_begin_time, order_pay_time,order_confirm_time " +
            "FROM " +
            "order_completed " +
            "WHERE uuid = #{uuid}")
    boolean moveOrderCompletedToOrderBegin(String uuid);

    @Select("select uuid from order_begin where begin_time < #{date}")
    List<String> getAllProcessExpiredOrdersByDate(String date);

    @Delete("DELETE FROM order_begin WHERE uuid = #{uuid}")
    public boolean deleteDataFromOrderBegin(String uuid);
}
