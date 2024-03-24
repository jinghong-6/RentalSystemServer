package com.example.rental.service.impl;

import com.example.rental.dao.SearchHistoryDao;
import com.example.rental.service.SearchHistoryService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchHistoryImpl implements SearchHistoryService {
    @Autowired
    private SearchHistoryDao searchHistoryDao;

    @Override
    public Result getConsumerSearchHistory(String consumerId) {
        List<Map<String,String>> searchHistoryMap = searchHistoryDao.getConsumerSearchHistory(consumerId);
        if (searchHistoryMap == null) {
            // 处理searchHistoryMap为null的情况
            return new Result(Code.SEARCH_ERR, "历史记录为空");
        } else {
            if (searchHistoryMap.isEmpty()) {
                return new Result(Code.SEARCH_ERR, "历史记录为空");
            } else {
                return new Result(Code.SEARCH_OK, searchHistoryMap);
            }
        }
    }
}
