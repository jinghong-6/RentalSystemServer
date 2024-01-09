package com.example.rental.dao.Alert;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface LandlordAlertDao {
    @Select("select * from landlord_alert where landlord_id = #{LandlordId}")
    List<Map<String, String>> getAlertListByLandlordId(String LandlordId);

    @Select("select * from landlord_alert where landlord_id = #{LandlordId} and id = #{AlertId}")
    Map<String, String> getAlertByLandlordIdAndAlertId(@Param("LandlordId") String LandlordId, @Param("AlertId") String AlertId);

    @Select("select count(*) from landlord_alert where landlord_id = #{LandlordId} and alert_status = '0'")
    Integer getAlertCountByLandlordId(String LandlordId);

    @Update("update landlord_alert set alert_status = '1' where landlord_id = #{LandlordId} and id = #{AlertId}")
    boolean updateLandlordAlertStatus(@Param("LandlordId") String LandlordId, @Param("AlertId") String AlertId);
}
