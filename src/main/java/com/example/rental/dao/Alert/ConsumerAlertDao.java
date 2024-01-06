package com.example.rental.dao.Alert;

import com.example.rental.domain.Alert.ConsumerAlert;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsumerAlertDao {
    @Insert("insert into consumer_alert values(#{id},#{consumer_id},#{title},#{content},#{alert_status},#{datetime})")
    Boolean InsertConsumerAlert(ConsumerAlert consumerAlert);
}
