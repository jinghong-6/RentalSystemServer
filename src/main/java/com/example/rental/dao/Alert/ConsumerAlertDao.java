package com.example.rental.dao.Alert;

import com.example.rental.domain.Alert.ConsumerAlert;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsumerAlertDao {
    @Select("select * from consumer_alert where consumer_id = #{ConsumerId}")
    List<Map<String,String>> getAlertListByConsumerId(String ConsumerId);

    @Select("select * from consumer_alert where consumer_id = #{ConsumerId} and id = #{AlertId}")
    Map<String,String> getAlertByConsumerIdAndAlertId(@Param("ConsumerId") String ConsumerId,@Param("AlertId") String AlertId);

    @Select("select count(*) from consumer_alert where consumer_id = #{ConsumerId} and alert_status = '0'")
    Integer getAlertCountByConsumerId(String ConsumerId);

    @Update("update consumer_alert set alert_status = '1' where consumer_id = #{ConsumerId} and id = #{AlertId}")
    boolean updateConsumerAlertStatus(@Param("ConsumerId") String ConsumerId,@Param("AlertId") String AlertId);

    @Insert("insert into consumer_alert values(#{id},#{consumer_id},#{title},#{content},#{alert_status},#{datetime})")
    boolean InsertConsumerAlert(ConsumerAlert consumerAlert);
}
