package com.example.rental.dao.Alert;

import com.example.rental.domain.Alert.LandlordAlert;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface LandlordAlertDao {
    @Select("select * from landlord_alert where landlord_id = #{LandlordId} ORDER BY create_time DESC")
    List<Map<String, String>> getAlertListByLandlordId(String LandlordId);

    @Select("select * from landlord_alert where landlord_id = #{LandlordId} and id = #{AlertId}")
    Map<String, String> getAlertByLandlordIdAndAlertId(@Param("LandlordId") String LandlordId, @Param("AlertId") String AlertId);

    @Select("select count(*) from landlord_alert where landlord_id = #{LandlordId} and alert_status = '0'")
    Integer getAlertCountByLandlordId(String LandlordId);

    @Update("update landlord_alert set alert_status = '1' where landlord_id = #{LandlordId} and id = #{AlertId}")
    boolean updateLandlordAlertStatus(@Param("LandlordId") String LandlordId, @Param("AlertId") String AlertId);

    @Insert("insert into landlord_alert values(#{id},#{landlord_id},#{title},#{content},#{alert_status},#{datetime})")
    boolean InsertLandlordAlert(LandlordAlert landlordAlert);
}
