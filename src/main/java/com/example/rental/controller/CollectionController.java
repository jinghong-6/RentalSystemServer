package com.example.rental.controller;

import com.example.rental.domain.Collection;
import com.example.rental.service.CollectionService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Autowired
    public CollectionService collectionService;

    @PostMapping()
    public Result getIdByHouseIdAndConsumerId(String houseId, String consumerId){
        return collectionService.getIdByHouseIdAndConsumerId(houseId, consumerId);
    }

    @PostMapping("/getCollection")
    public Result getHouseIdByConsumerId(String consumerId){
        return collectionService.getHouseIdByConsumerId(consumerId);
    }

    @PostMapping("/setCollection")
    public Result setCollection(Collection collection){
        return collectionService.InsertCollection(collection);
    }

    @PostMapping("/delCollection")
    public Result delCollection(String houseId, String consumerId){
        return collectionService.deleteCollection(houseId, consumerId);
    }
}
