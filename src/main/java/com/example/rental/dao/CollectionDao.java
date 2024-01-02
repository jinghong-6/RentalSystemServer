package com.example.rental.dao;

import com.example.rental.domain.Collection;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CollectionDao {
    @Select("select * from collection")
     List<Collection> getAllCollection();

    @Select("select house_id from collection where consumer_id = #{consumerId}")
     List<String> getHouseIdByConsumerId(String consumerId);

    @Select("select consumer_id from collection where house_id = #{houseId}")
    List<String> getConsumerIdByHouseId(String houseId);

    @Select("select id from collection where house_id = #{houseId} and consumer_id = #{consumerId}")
     Boolean getIdByHouseIdAndConsumerId(@Param("houseId") String houseId, @Param("consumerId") String consumerId);

    @Insert("insert into collection values(#{id},#{consumer_id},#{house_id},#{collection_time})")
     Boolean InsertCollection(Collection collection);

    @Delete("DELETE FROM collection WHERE house_id = #{houseId} and consumer_id = #{consumerId}")
     Boolean deleteCollection(@Param("houseId") String houseId, @Param("consumerId") String consumerId);
}
