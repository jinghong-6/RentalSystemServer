package com.example.rental.dao;

import com.example.rental.domain.House;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
public interface HouseDao {
    @Select("select city_id,type from house where id = #{id}")
    Map<String, String> getCityAndTypeById(String id);

    @Select("select " +
            "id,house_name,price,firstImg,city_id,introduce " +
            "from house " +
            "where house_status = '0' " +
            "ORDER BY RAND() " +
            "limit 8")
    List<House> getHouseRand();

    @Select("select " +
            "id,house_name,price,firstImg,city_id,introduce " +
            "from house " +
            "where type = #{type} and house_status = '0' " +
            "ORDER BY RAND() " +
            "limit #{num}")
    List<House> getHouseByTypeRand(@Param("type") String type, @Param("num") int num);

    @Select("select id from house where landlord_id = #{landlord_id}")
    List<String> getHouseIdByLandlordId(String landlord_id);

    @Select("SELECT id, house_name, price, firstImg, city_id, introduce " +
            "FROM house " +
            "WHERE city_id = #{cityId} and house_status = '0'")
    List<House> getHouseByCityId(String cityIds);

    @Select("select " +
            "id,house_name,price,firstImg,city_id,introduce " +
            "from house " +
            "where house_status = '0' " +
            "limit #{BeginIndex},#{EndIndex}")
    List<House> getHouseByIndex(@Param("BeginIndex") int BeginIndex, @Param("EndIndex") int EndIndex);

    @Select("select " +
            "id,house_name,price,firstImg,city_id,introduce " +
            "from house " +
            "where type = #{Type} and house_status = '0' " +
            "limit #{BeginIndex},#{EndIndex}")
    List<House> getHouseByTypeAndIndex(@Param("Type") String Type, @Param("BeginIndex") int BeginIndex, @Param("EndIndex") int EndIndex);

    @Select("SELECT id,house_name,price,firstImg,city_id,introduce " +
            "FROM house " +
            "WHERE house_name LIKE CONCAT('%', #{search_value}, '%') OR introduce LIKE CONCAT('%', #{search_value}, '%') and house_status = '0' " +
            "limit #{BeginIndex},#{EndIndex}")
    List<House> getHouseBySearchValue(@Param("search_value") String search_value, @Param("BeginIndex") int BeginIndex, @Param("EndIndex") int EndIndex);

    @Select("select id,house_name,price,firstImg,city_id,introduce " +
            "from house " +
            "where city_id in (select id from city where provinceZh = #{city}) and house_status = '0' " +
            "limit #{BeginIndex},#{EndIndex}")
    List<House> getHouseByCity(@Param("city") String city, @Param("BeginIndex") int BeginIndex, @Param("EndIndex") int EndIndex);

    @Select("select * from house where id = #{houseId}")
    House getHouseById(String houseId);

    @Select("select house_name,firstImg from house where id = #{id}")
    Map<String, Object> getHouseNameAndImgById(String id);

    @Select("select id,house_name,price,firstImg,begin_time,end_time,type,max_num,price,house_status " +
            "from house " +
            "where landlord_id = #{landlordId}")
    List<Map<String, Object>> getHouseByLandlordId(String landlordId);

    @Select("select id from house where end_time < #{time} and house_status = '0'")
    List<String> getOverHouseList(String time);

    @Select("select id from house where end_time < #{time} and house_status != '0' and id = #{id}")
    List<String> getOverHouseList2(@Param("time") String time, @Param("id") String id);

    @Select("select id from house where end_time > #{time} and house_status = '2'")
    List<String> getunOverHouseList(String time);

    @Update("update house set house_status = #{status} where id = #{id}")
    boolean UpdateHouseStatusById(@Param("status") String status, @Param("id") String id);

    @Insert("insert into house " +
            "values(" +
            "#{id},#{landlord_id},#{house_name},#{introduce},#{price},#{city_id},#{full_address}," +
            "#{firstImg},#{img},#{begin_time},#{end_time},#{service},#{type},#{max_num},0" +
            ")")
    boolean InsertHouse(House house);

    @Update("update house " +
            "set " +
            "house_name=#{house_name},introduce=#{introduce},price=#{price},city_id=#{city_id}," +
            "full_address=#{full_address},firstImg=#{firstImg},img=#{img},service=#{service}," +
            "type=#{type},max_num=#{max_num} where id=#{id}")
    boolean UpdateHouseById(House house);
}
