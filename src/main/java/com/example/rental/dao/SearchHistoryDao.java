package com.example.rental.dao;

import com.example.rental.domain.House;
import com.example.rental.domain.SearchHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SearchHistoryDao {
    @Select("select keyword from search_history where consumer_id = #{consumerId}  order by search_time desc")
    List<Map<String,String>> getConsumerSearchHistory(String consumerId);

    @Insert("insert into search_history " +
            "(consumer_id,keyword) " +
            "values(" +
            "#{consumer_id},#{keyword}" +
            ")")
    void InsertSearch(SearchHistory searchHistory);

    @Delete("DELETE FROM search_history " +
            "WHERE consumer_id = #{consumerId} " +
            "and id NOT IN ( " +
            "SELECT t.id " +
            "FROM " +
            "( " +
            "select * from search_history where consumer_id = #{consumerId} ORDER BY id DESC LIMIT 20 " +
            ") as t" +
            ")")
    Boolean deleteSearchKeyword(String consumerId);
}
