package com.example.rental.service.impl;

import com.example.rental.dao.CollectionDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.domain.Collection;
import com.example.rental.domain.House;
import com.example.rental.service.CollectionService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionImpl implements CollectionService {
    @Autowired
    public CollectionDao collectionDao;

    @Autowired
    public HouseDao houseDao;

    @Override
    public Result getAllCollection() {
        return null;
    }

    @Override
    public Result getHouseIdByConsumerId(String consumerId) {
        List<String> houseIdList = collectionDao.getHouseIdByConsumerId(consumerId);
        if (houseIdList != null && houseIdList.size() != 0) {
            List<House> houseList = new ArrayList<>();
            for (String id : houseIdList) {
                houseList.add(houseDao.getHouseById(id));
            }
            return new Result(Code.SEARCH_OK,houseList);
        } else {
            return new Result(Code.SEARCH_ERR, "未收藏");
        }

    }

    @Override
    public Result getConsumerIdByHouseId(String houseId) {
        return null;
    }

    @Override
    public Result getIdByHouseIdAndConsumerId(String houseId, String consumerId) {
        Boolean flag = collectionDao.getIdByHouseIdAndConsumerId(houseId, consumerId);
        if (flag != null) {
            return new Result(Code.SEARCH_OK, "已收藏");
        } else {
            return new Result(Code.SEARCH_ERR, "未收藏");
        }
    }

    /**
     * 将收藏插入系统，针对特定的民宿和消费者。
     *
     * @param collection 包含收藏信息的 Collection 对象。
     * @return 返回表示收藏插入状态的 Result：
     * - SEARCH_OK 表示收藏已存在（避免重复收藏）。
     * - SAVE_OK 表示成功插入收藏。
     * - SAVE_ERR 表示在插入收藏过程中出现错误。
     */
    @Override
    public Result InsertCollection(Collection collection) {
        if (collection.getHouse_id()==null || collection.getConsumer_id() == null){
            return new Result(Code.SAVE_ERR, "收藏失败,不能为空");
        }
        Boolean isAlreadyCollected = collectionDao.getIdByHouseIdAndConsumerId(collection.getHouse_id(), collection.getConsumer_id());
        if (isAlreadyCollected != null) {
            return new Result(Code.SEARCH_OK, "已收藏,请勿重复收藏");
        } else {
            LocalDateTime currentTime = LocalDateTime.now();
            // 定义日期时间格式化器，用于解析目标时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            collection.setCollection_time(currentTime.format(formatter));
            boolean isInserted = collectionDao.InsertCollection(collection);
            if (isInserted) {
                return new Result(Code.SAVE_OK, "成功收藏");
            } else {
                return new Result(Code.SAVE_ERR, "收藏失败");
            }
        }
    }

    @Override
    public Result deleteCollection(String houseId, String consumerId) {
        boolean isDeled = collectionDao.deleteCollection(houseId, consumerId);
        if (isDeled) {
            return new Result(Code.DELETE_OK, "删除成功");
        } else {
            return new Result(Code.DELETE_ERR, "删除失败");
        }
    }
}
