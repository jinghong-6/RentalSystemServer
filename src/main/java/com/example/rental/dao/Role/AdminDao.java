package com.example.rental.dao.Role;

import com.example.rental.domain.Role.Admin;
import com.example.rental.domain.Role.Consumer;
import com.example.rental.domain.Role.Landlord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminDao {
    @Select("select pwd from admin where admin_account = #{Account}")
    String getPwdByAccount(String Account);

    @Select("select " +
            "id,admin_name,admin_account,money " +
            "from admin " +
            "where " +
            "admin_account = #{Account}")
    Admin getAdminInfoByAccount(String Account);

    @Select("select " +
            "id,tele,consumer_name,qq,wechat,province,county,img_url,consumer_status,money,register_time " +
            "from consumer")
    List<Consumer> getAllConsumer();

    @Update("update consumer set consumer_status = #{status} where id = #{id}")
    boolean UpdateConsumerStatusById(@Param("status") String status, @Param("id") String id);

    @Select("select " +
            "id,tele,landlord_name,qq,wechat,province,county,img_url,landlord_status,register_time " +
            "from landlord")
    List<Landlord> getAllLandlord();

    @Update("update consumer set consumer_status = #{status} where id = #{id}")
    boolean UpdateLandlordStatusById(@Param("status") String status, @Param("id") String id);

    @Select("SELECT " +
            "(SELECT COUNT(*) FROM order_begin) + " +
            "(SELECT COUNT(*) FROM order_end) + " +
            "(SELECT COUNT(*) FROM order_complete) + " +
            "(SELECT COUNT(*) FROM order_completed) + " +
            "(SELECT COUNT(*) FROM order_nopay) AS num")
    Map<String, Object> getOrderNum();

    @Select("select count(*) as num from consumer")
    Map<String,Object> getConsumerNum();

    @Select("select count(*) as num from landlord")
    Map<String,Object> getLandlordNum();

    @Select("select count(*) as num from house")
    Map<String,Object> getHouseNum();

    @Select("select id from house")
    List<String> getHouseId();

    @Select("SELECT DATE_FORMAT(begin_time, '%Y-%m') AS month, COUNT(*) AS count,SUM(CAST(admin_money AS DECIMAL(10,2))) AS total_price " +
            "FROM ( " +
            "    SELECT begin_time,CAST(admin_money AS DECIMAL(10,2)) AS admin_money FROM order_begin " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(admin_money AS DECIMAL(10,2)) AS admin_money FROM order_complete " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(admin_money AS DECIMAL(10,2)) AS admin_money FROM order_completed " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(admin_money AS DECIMAL(10,2)) AS admin_money FROM order_nopay " +
            "    UNION ALL " +
            "    SELECT begin_time,CAST(admin_money AS DECIMAL(10,2)) AS admin_money FROM order_end " +
            ") AS combined_tables " +
            "GROUP BY month")
    List<Map<String, String>> getOrderDateCount();

    @Select("select id,landlord_id,house_name,price,firstImg,begin_time,end_time,type,max_num,price,house_status from house")
    List<Map<String, Object>> getHouseByAdmin();

    @Update("update admin set money = #{money}")
    boolean updateAdminMoney(String money);
}
