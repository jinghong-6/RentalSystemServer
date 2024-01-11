package com.example.rental.service.impl;

import com.example.rental.dao.CommentDao;
import com.example.rental.dao.Order.OrderEndDao;
import com.example.rental.dao.Role.ConsumerDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.domain.Comment;
import com.example.rental.service.CommentService;
import com.example.rental.service.impl.Alert.LandlordAlertServiceImpl;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private LandlordDao landlordDao;

    @Autowired
    private ConsumerDao consumerDao;

    @Autowired
    private OrderEndDao orderEndDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private LandlordAlertServiceImpl landlordAlertService;

    @Override
    public Result getCommentByHouseId(String houseId) {
        List<Map<String, Object>> AllComment = new ArrayList<>();
        List<Map<String,String>> uuidList = orderEndDao.getUuidByHouseId(houseId);
        for (Map<String,String> infoMap : uuidList){
            String uuid = infoMap.get("uuid");
            String grades = infoMap.get("grades");
            List<Comment> commentList = commentDao.getAllCommentByUUID(uuid);

            for (Comment comment: commentList){
                if (comment.getUser_type().equals("0")){
                    Map<String,String> UserImgAndName = consumerDao.getUserImgAndNameById(comment.getUser_id());
                    comment.setUser_id(UserImgAndName.get("consumer_name"));
                    comment.setUser_type(UserImgAndName.get("img_url"));
                }
                if (comment.getUser_type().equals("1")){
                    Map<String,String> UserImgAndName = landlordDao.getLandImgAndNameById(comment.getUser_id());
                    comment.setUser_id(UserImgAndName.get("landlord_name"));
                    comment.setUser_type(UserImgAndName.get("img_url"));
                }
                comment.setGrades(grades);
                if (comment.getParent_id() == null){
                    commentList.remove(comment);
                    commentList.add(0, comment);
                }
            }
            System.out.println(commentList);
            List<Map<String, Object>> newComment = organizeComments(commentList);
            System.out.println("66");
            System.out.println(newComment);
            AllComment.addAll(newComment);
        }
//        System.out.println(AllComment);
        if (uuidList.size() == 0){
            return new Result(Code.SEARCH_OK,"目前还没有评论");
        }
        return new Result(Code.SEARCH_OK,AllComment);
    }

    @Override
    public Result getCommentsByUUID(String UUID) {
        List<Comment> commentList = commentDao.getAllCommentByUUID(UUID);
        for (Comment comment: commentList){
            if (comment.getParent_id() == null){
                commentList.remove(comment);
                commentList.add(0, comment);
            }
        }
        List<Map<String, Object>> newComment = organizeComments(commentList);

        return new Result(Code.SEARCH_OK,newComment);
    }

    @Override
    public Result InsertComment(Comment comment) {
        System.out.println(comment);
        if (commentDao.getSameUUIDCommentByUUID(comment.getUuid()) != null && comment.getParent_id() == null){
            return new Result(Code.UPDATE_ERR,"请勿重复评论");
        }
        if (!commentDao.InsertComment(comment)){
            return new Result(Code.UPDATE_ERR,"评论失败");
        }else {
            landlordAlertService.addLandlordAlert("1",comment.getUuid());
            return new Result(Code.UPDATE_OK,"评论成功");
        }
    }

    /**
     * 格式化评论的层次结构
     * @param comments 要格式化的评论列表
     * @return 格式化好的评论列表
     */
    private static List<Map<String, Object>> organizeComments(List<Comment> comments) {
        // 用于快速访问基于ID的评论
        Map<String, Map<String, Object>> commentMap = new HashMap<>();

        // 最终列表，用于保存组织好的评论
        List<Map<String, Object>> organizedComments = new ArrayList<>();

        for (Comment comment : comments) {
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("id", comment.getId());
            commentData.put("parent_id", comment.getParent_id());
            commentData.put("uuid", comment.getUuid());
            commentData.put("content", comment.getContent());
            commentData.put("grades", comment.getGrades());
            commentData.put("user_id", comment.getUser_id());
            commentData.put("user_type", comment.getUser_type());
            commentData.put("create_time", comment.getCreate_time());
            commentData.put("imgs", comment.getImgs());
            commentData.put("children", new ArrayList<Map<String, Object>>());

            // 将评论添加到映射以便以后引用
            commentMap.put(comment.getId(), commentData);

            // 检查评论是否有父级
            if (comment.getParent_id() == null) {
                // 父级评论
                organizedComments.add(commentData);
            } else {
                // 子级评论
                Map<String, Object> parentComment = commentMap.get(comment.getParent_id());
                if (parentComment != null) {
                    // 寻找总的父评论（最顶层的父评论）
                    String rootParentId = findRootParent(comment.getParent_id(), commentMap, new HashSet<>());
                    Map<String, Object> rootParentComment = commentMap.get(rootParentId);

                    // 将子评论添加到总的父评论的 'children' 列表中
                    List<Map<String, Object>> children = (List<Map<String, Object>>) rootParentComment.get("children");
                    children.add(commentData);

                    // 对子评论列表按照创建时间排序
                    sortCommentsByCreateTime(children);
                }
            }
        }

        return organizedComments;
    }

    /**
     * 递归寻找总的父评论的ID
     * @param parentId 当前评论的父评论ID
     * @param commentMap 存储评论的映射
     * @param visited 记录已访问的评论ID的集合，防止无限递归
     * @return 总的父评论的ID
     */
    private static String findRootParent(String parentId, Map<String, Map<String, Object>> commentMap, Set<String> visited) {
        if (visited.contains(parentId)) {
            // 防止无限递归
            return parentId;
        }

        visited.add(parentId);

        Map<String, Object> parentComment = commentMap.get(parentId);
        if (parentComment != null && parentComment.containsKey("parent_id") && parentComment.get("parent_id") != null) {
            return findRootParent((String) parentComment.get("parent_id"), commentMap, visited);
        }

        return parentId;
    }

    /**
     * 对评论列表按照创建时间排序
     * @param comments 要排序的评论列表
     */
    private static void sortCommentsByCreateTime(List<Map<String, Object>> comments) {
        comments.sort((comment1, comment2) -> {
            String createTime1 = (String) comment1.get("create_time");
            String createTime2 = (String) comment2.get("create_time");
            // 假设时间格式是 "yyyy-MM-dd HH:mm:ss"，你可能需要根据实际情况调整
            return createTime1.compareTo(createTime2);
        });
    }
}
