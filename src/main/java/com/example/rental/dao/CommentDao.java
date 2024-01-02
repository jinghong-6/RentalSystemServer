package com.example.rental.dao;

import com.example.rental.domain.Comment;
import com.example.rental.domain.House;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentDao {
    @Select("select * from comment where uuid = #{UUID} order by create_time desc")
    List<Comment> getAllCommentByUUID(String UUID);

    @Select("select * from comment where uuid = #{UUID} and parent_id IS NULL")
    Boolean getSameUUIDCommentByUUID(String UUID);

    @Insert("insert into comment " +
            "(uuid,content,parent_id,user_id,user_type,imgs)" +
            "values(" +
            "#{uuid},#{content},#{parent_id},#{user_id},#{user_type},#{imgs}" +
            ")")
    boolean InsertComment(Comment comment);
}
