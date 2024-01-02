package com.example.rental.dao;

import com.example.rental.domain.House;
import com.example.rental.domain.SearchHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SearchHistoryDao {
    @Insert("insert into search_history " +
            "(consumer_id,keyword) " +
            "values(" +
            "#{consumer_id},#{keyword}" +
            ")")
    public void InsertSearch(SearchHistory searchHistory);

    @Delete("DELETE FROM search_history " +
            "WHERE consumer_id = #{consumerId} " +
            "and id NOT IN ( " +
            "SELECT t.id " +
            "FROM " +
            "( " +
            "select * from search_history where consumer_id = #{consumerId} ORDER BY id DESC LIMIT 20 " +
            ") as t" +
            ")")
    public Boolean deleteSearchKeyword(String consumerId);
}
