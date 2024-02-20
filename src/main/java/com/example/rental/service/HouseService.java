package com.example.rental.service;

import com.example.rental.domain.House;
import com.example.rental.utils.Result;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HouseService {
    //    推荐民宿
    Result getHouseRand(String consumerId);

    //    查询民宿列表
    List<House> getHouseByIndex(String index);

    //    查询民宿列表（类型）
    List<House> getHouseByTypeAndIndex(String Type, String index);

    //    查询民宿列表（关键词）
    List<House> getHouseBySearchValue(String consumerId, String search_value, String index);

    //    查询民宿列表（城市）
    List<House> getHouseByCity(String city, String index);

    //    通过id查询民宿
    Map<String, Object> getHouseById(String houseId);

    //    通过房东id查民宿
    Result getHouseByLandlordId(String landlordId);

    //    新增民宿
    Result InsertHouse(House house);

    //    修改民宿
    Result UpdateHouseById(House house);

    //    上下架民宿
    Result UpdateHouseStatusById(String status, String id);
}
