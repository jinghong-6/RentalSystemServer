package com.example.rental.service;

import java.util.List;
import java.util.Map;

public interface CityService {
//    查询所有省份，及其以下县市
    List<String> getAllCity();

//    查询所有省份
    List<String> getAllProvince();

//    通过省份查询市
    List<String> getAllLeaderCityByProvince(String province);

//    通过市查询县市
    List<String> getAllCityByLeaderCity(String LeaderCity);

//    通过城市查询id
    String getCityIdByCity(String cityZh,String provinceZh,String leaderZh);
}