package com.example.rental.service;

import com.example.rental.domain.Comment;
import com.example.rental.utils.Result;

public interface CommentService {
    //  获取当前民宿的评论
    Result getCommentByHouseId(String houseId);

    //  获取当前订单的评论
    Result getCommentsByUUID(String UUID);

    //  插入评论
    Result InsertComment(Comment comment);
}
