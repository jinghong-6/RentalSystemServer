package com.example.rental.service;

import com.example.rental.domain.Collection;
import com.example.rental.utils.Result;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectionService {
    //  获取所有收藏
    public Result getAllCollection();

    //  获取用户收藏的民宿
    public Result getHouseIdByConsumerId(String consumerId);

    //  获取一个民宿被收藏的用户列表
    public Result getConsumerIdByHouseId(String houseId);

    //  查询当前民宿是否被当前用户收藏
    public Result getIdByHouseIdAndConsumerId(String houseId,String consumerId);

    //  新增收藏
    public Result InsertCollection(Collection collection);

    //  删除收藏
    public Result deleteCollection(String houseId,String consumerId);
}
