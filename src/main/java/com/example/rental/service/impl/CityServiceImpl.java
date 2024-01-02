package com.example.rental.service.impl;

import com.example.rental.dao.CityDao;
import com.example.rental.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CityServiceImpl implements CityService {
    @Autowired
    private CityDao cityDao;

//    @Override
//    public Map<String, List> getAllCity() {
//        List<String> list = cityDao.getAllProvince();
//        Map<String,List> map = new HashMap<>();
//        for (String obj:list){
//            map.put(obj,cityDao.getAllCityByProvince(obj));
//        }
//        return map;
//    }

    @Override
    public List<String> getAllCity() {
        return cityDao.getAllProvince();
    }

    @Override
    public List<String> getAllProvince() {
        return cityDao.getAllProvince();
    }

    @Override
    public List<String> getAllLeaderCityByProvince(String province) {
        return cityDao.getAllLeaderCity(province);
    }

    @Override
    public List<String> getAllCityByLeaderCity(String LeaderCity) {
        return cityDao.getAllCityByLeaderZh(LeaderCity);
    }

    @Override
    public String getCityIdByCity(String cityZh, String provinceZh, String leaderZh) {
        return cityDao.getCityIdByCity(cityZh,provinceZh,leaderZh);
    }
}
