package com.example.rental.controller;

import com.example.rental.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/city")
public class CityController {
    @Autowired
    private CityService cityService;

    @GetMapping()
    public  List<String> getAllCity(){
        return cityService.getAllCity();
    }

    @PostMapping("/allProvince")
    public List<String> getAllProvince(){
        return cityService.getAllProvince();
    }

    @PostMapping ("/allLeaderCity")
    public List<String> getAllLeaderCityByProvince(String province){
        return cityService.getAllLeaderCityByProvince(province);
    }

    @PostMapping("/allCityByLeaderCity")
    public List<String> getAllCityByLeaderCity(String LeaderCity){
        return cityService.getAllCityByLeaderCity(LeaderCity);
    }

    @PostMapping("/getId")
    public String getCityIdByCity(String cityZh,String provinceZh,String leaderZh){
        return cityService.getCityIdByCity(cityZh,provinceZh,leaderZh);
    }
}
