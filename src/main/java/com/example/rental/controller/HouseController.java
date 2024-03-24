package com.example.rental.controller;

import com.example.rental.domain.House;
import com.example.rental.service.HouseService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/house")
public class HouseController {
    @Autowired
    private HouseService houseService;

    @PostMapping("/getHouse/rand")
    public Result getHouseRand(String consumerId){
        return houseService.getHouseRand(consumerId);
    }

    @GetMapping("/getHouse/{houseId}")
    public Result getHouseById(@PathVariable String houseId){
        return new Result(Code.SEARCH_OK,houseService.getHouseById(houseId));
    }

    @PostMapping("/List")
    public Result getHouseByIndex(String index){
        return new Result(Code.SEARCH_OK,houseService.getHouseByIndex(index));
    }

    @PostMapping("/List/Type")
    public Result getHouseByIndex(String Type,String index){
        return new Result(Code.SEARCH_OK,houseService.getHouseByTypeAndIndex(Type,index));
    }

    @PostMapping("/List/SearchValue")
    public Result getHouseBySearchValue(String consumerId,String searchValue,String index){
        return new Result(Code.SEARCH_OK,houseService.getHouseBySearchValue(consumerId,searchValue,index));
    }

    @PostMapping("/List/City")
    public Result getHouseByCity(String city,String index){
        return new Result(Code.SEARCH_OK,houseService.getHouseByCity(city,index));
    }

    @PostMapping("/LandlordId")
    public Result getHouseByLandlordId(String landlordId){
        return houseService.getHouseByLandlordId(landlordId);
    }

    @PostMapping()
    public Result InsertHouse(House house){
        return houseService.InsertHouse(house);
    }

    @PostMapping("/Update")
    public Result UpdateHouse(House house){
        return houseService.UpdateHouseById(house);
    }

    @PostMapping("/UpdateStatus")
    public Result UpdateHouseStatusById(String status, String id){
        return houseService.UpdateHouseStatusById(status,id);
    }
}
