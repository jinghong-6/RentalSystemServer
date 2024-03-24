package com.example.rental.controller;

import com.example.rental.service.SearchHistoryService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
public class SearchHistoryController {
    @Autowired
    private SearchHistoryService searchHistoryService;

    @PostMapping()
    public Result getConsumerSearchHistory(String consumerId){
        return searchHistoryService.getConsumerSearchHistory(consumerId);
    }
}
