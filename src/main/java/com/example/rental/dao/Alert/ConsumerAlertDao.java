package com.example.rental.dao.Alert;

import com.example.rental.domain.Alert.ConsumerAlert;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsumerAlertDao {
    @Select("select * from consumer_alert where consumer_id = #{ConsumerId}")
    List<Map<String,String>> getAlertByConsumerId(String ConsumerId);

    @Select("select count(*) from consumer_alert where consumer_id = #{ConsumerId} and alert_status = '0'")
    Integer getAlertCountByConsumerId(String ConsumerId);

    @Insert("insert into consumer_alert values(#{id},#{consumer_id},#{title},#{content},#{alert_status},#{datetime})")
    Boolean InsertConsumerAlert(ConsumerAlert consumerAlert);
}
