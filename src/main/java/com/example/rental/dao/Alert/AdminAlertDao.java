package com.example.rental.dao.Alert;

import com.example.rental.domain.Alert.AdminAlert;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminAlertDao {
    @Select("select * from admin_alert ORDER BY create_time DESC")
    List<AdminAlert> getAllAdminAlert();

    @Select("select * from admin_alert where id = #{AlertId}")
    AdminAlert getAdminAlertById(String AlertId);

    @Update("update admin_alert set alert_status = '1' where id = #{AlertId}")
    boolean updateConsumerAlertStatus(String AlertId);

    @Insert("insert into admin_alert values(#{id},#{content},#{alert_status},#{user_id},#{user_type},#{create_time})")
    boolean InsertAdminAlert(AdminAlert adminAlert);
}
