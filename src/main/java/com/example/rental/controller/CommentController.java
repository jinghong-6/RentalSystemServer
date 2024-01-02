package com.example.rental.controller;

import com.example.rental.domain.Comment;
import com.example.rental.service.CommentService;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/HouseId")
    public Result getCommentByHouseId(String houseId){
        return commentService.getCommentByHouseId(houseId);
    }

    @PostMapping("/uuid")
    public Result getCommentByUUID(String UUID){
        return commentService.getCommentsByUUID(UUID);
    }

    @PostMapping("/setComment")
    public Result getCommentByUUID(Comment comment){
        return commentService.InsertComment(comment);
    }
}
