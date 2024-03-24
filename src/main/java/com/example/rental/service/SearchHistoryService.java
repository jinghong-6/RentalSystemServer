package com.example.rental.service;

import com.example.rental.utils.Result;

public interface SearchHistoryService {
    //      查询用户历史搜索记录
    Result getConsumerSearchHistory(String consumerId);
}
