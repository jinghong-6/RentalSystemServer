package com.example.rental.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CityDao {
    @Select("select distinct provinceZh from city")
    public List<String> getAllProvince();

    @Select("select provinceZh,cityZh from city where id = #{id}")
    public Map<String,String> getCityNameById(String id);

    @Select("select provinceZh from city where id = #{id}")
    public List<String> getAllProvinceZhById(String id);

    @Select("select distinct leaderZh from city where provinceZh = #{provinceZh}")
    public List<String> getAllLeaderCity(String provinceZh);

    @Select("select cityZh from city where leaderZh = #{leaderZh}")
    public List<String> getAllCityByLeaderZh(String leaderZh);

    @Select("select id from city " +
            "where " +
            "provinceZh = #{provinceZh}")
    public List<String> getCityIdListByProvinceZh(String provinceZh);

    @Select("select id from city " +
            "where " +
            "cityZh = #{cityZh} and " +
            "provinceZh = #{provinceZh} and " +
            "leaderZh = #{leaderZh}")
    public String getCityIdByCity(@Param("cityZh") String cityZh, @Param("provinceZh") String provinceZh, @Param("leaderZh") String leaderZh);

    @Select("select lat,lon,provinceZh,leaderZh,cityZh from city where id = #{id}")
    public Map<String,String> getLatAndLonById(String id);
}
