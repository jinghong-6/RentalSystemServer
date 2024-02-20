package com.example.rental.service.impl.Role;

import com.alibaba.fastjson.JSONObject;
import com.example.rental.dao.CollectionDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderEndDao;
import com.example.rental.dao.Role.ConsumerDao;
import com.example.rental.domain.House;
import com.example.rental.domain.Role.Consumer;
import com.example.rental.service.Role.ConsumerService;
import com.example.rental.utils.Code;
import com.example.rental.utils.JWTUtils;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.rental.utils.CharacterFilter;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    @Autowired
    private ConsumerDao consumerDao;

    @Autowired
    private OrderEndDao orderEndDao;

    @Autowired
    private CollectionDao collectionDao;

    @Autowired
    private HouseDao houseDao;

    @Override
    public Result getUserInfo(String ConsumerId) {
        Map<String,Object> map = new HashMap<>();
        //  获取评论数
        List<Map<String, Object>> NotRatedOrders = orderEndDao.getNotRatedCommentOrderByConsumerId(ConsumerId,"0");
        List<Map<String, Object>> RatedOrders = orderEndDao.getRatedCommentOrderByConsumerId(ConsumerId);
        String NotRatedNum = String.valueOf(NotRatedOrders.size());
        String RatedNum = String.valueOf(RatedOrders.size());

        //  获取收藏数
        String Collection = String.valueOf(collectionDao.getHouseIdByConsumerId(ConsumerId).size());

        //  获取订单数
        Map <String,String> OrderListNum = orderEndDao.getOrderEndNumByConsumerId(ConsumerId);

        //  获取收藏民宿类型
        List<Map<String, String>> CollectionHouse = getCountList(collectionDao.getHouseIdByConsumerId(ConsumerId));

        //  获取订单民宿类型
        List<Map<String, String>> OrderHouse = getCountList(orderEndDao.getHouseIdByConsumerId(ConsumerId));

        //  获取每月订单数和总价
        List<Map<String, String>> OrderDateCountList = orderEndDao.getOrderDateCountByConsumerId(ConsumerId);
        List<String> OrderMonth = new ArrayList<>();
        List<String> OrderCount = new ArrayList<>();
        List<String> AllPrice = new ArrayList<>();
        for (Map<String,String> orderDate : OrderDateCountList){
            OrderMonth.add(orderDate.get("month"));
            OrderCount.add(orderDate.get("count"));
            AllPrice.add(orderDate.get("total_price"));
        }

        map.putAll(OrderListNum);
        map.put("collectionMap",CollectionHouse);
        map.put("OrderHouseMap",OrderHouse);
        map.put("NotRatedNum",NotRatedNum);
        map.put("RatedNum",RatedNum);
        map.put("Collection",Collection);
        map.put("OrderMonth",OrderMonth);
        map.put("OrderCount",OrderCount);
        map.put("AllPrice",AllPrice);
        return new Result(Code.SEARCH_OK,map);
    }

    private List<Map<String, String>> getCountList(List<String> houseIdList) {
        List<Map<String, String>> resultList = new ArrayList<>();
        List<String> houseTypeList = new ArrayList<>();

        // 获取房屋类型列表
        for (String houseId : houseIdList) {
            houseTypeList.add(houseDao.getCityAndTypeById(houseId).get("type"));
        }

        // 遍历类型列表进行计数
        Map<String, Integer> typeCountMap = new HashMap<>();
        for (String type : houseTypeList) {
            typeCountMap.put(type, typeCountMap.getOrDefault(type, 0) + 1);
        }

        // 将计数结果放入 resultList
        for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("name", entry.getKey());
            resultMap.put("value", String.valueOf(entry.getValue()));
            resultList.add(resultMap);
        }

        return resultList;
    }

    // 查看是否有相同的账户
    @Override
    public Result getSameAccount(String tele) {
        String filtered = CharacterFilter.filterSpecialCharacters(tele);
        if (consumerDao.getSameAccount(filtered) == 0){
            System.out.println("用户注册");
            String AccountToken = JWTUtils.getRegisterToken(filtered,"user");
            return new Result(Code.SEARCH_OK,AccountToken);
        }else {
            return new Result(Code.SAVE_ERR,"500");
        }
    }

    // 用户注册
    @Override
    public Result userRegister(Consumer consumer) {
        //  注册时间
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(date);

        consumer.setImg_url("http://zzw.fj.cn/p/group1/M00/00/00/CqgBuWTEwIiAGdpFAABvCRM88IY897.jpg");
        consumer.setConsumer_status(0);
        consumer.setRegister_time(time);
        consumer.setLogin_status(0);
        consumer.setMoney("0");
        consumer.setPay_pwd("111111");

        System.out.println(consumer);

        String tele = consumer.getTele();
        if (consumerDao.getSameAccount(tele) > 0){
            return new Result(Code.SAVE_ERR,false);
        }

        if (consumerDao.setAccount(consumer)){
            return new Result(Code.SAVE_OK,true);
        }else {
            return new Result(Code.SAVE_ERR,false);
        }
    }

    // 通过账号获取密码
    @Override
    public Result userLogin(String tele,String pwd) {
        if (consumerDao.getPwdByTele(tele) != null && consumerDao.getPwdByTele(tele).equals(pwd)){
            //    获取用户信息
            Consumer consumer = consumerDao.getUserInfoByTele(tele);
            //    获取token
            String accessToken = JWTUtils.getLoginAccessToken(consumer.getTele(),"consumer",consumer.getConsumer_name());
            String refreshToken = JWTUtils.getLoginRefreshToken(consumer.getTele(),"consumer",consumer.getConsumer_name());

            JSONObject json = new JSONObject();
            json.put("consumer",consumer);
            json.put("accessToken",accessToken);
            json.put("refreshToken",refreshToken);

            return new Result(Code.SEARCH_OK,json);
        }else {
            return new Result(Code.SEARCH_ERR,"登录失败");
        }
    }

    @Override
    public Result userAutoLogin(String tele) {
        //    获取用户信息
        Consumer consumer = consumerDao.getUserInfoByTele(tele);
        //    获取token
        String accessToken = JWTUtils.getLoginAccessToken(consumer.getTele(),"consumer",consumer.getConsumer_name());
        String refreshToken = JWTUtils.getLoginRefreshToken(consumer.getTele(),"consumer",consumer.getConsumer_name());

        JSONObject json = new JSONObject();
        json.put("consumer",consumer);
        json.put("accessToken",accessToken);
        json.put("refreshToken",refreshToken);

        return new Result(Code.SEARCH_OK,json);
    }

    @Override
    public Result UpdateConsumerInfo(Consumer consumer) {
        boolean updateResult = consumerDao.UpdateConsumerInfo(consumer);
        if (updateResult){
            return new Result(Code.UPDATE_OK,"更新成功");
        }else {
            return new Result(Code.UPDATE_ERR,"更新失败");
        }
    }
}
