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

    /**
     * 获取用户详细信息方法，包括评论数、收藏数、订单数、收藏的民宿类型、订单的民宿类型以及每月订单数和总价。
     *
     * @param ConsumerId 用户ID
     * @return 结果对象，包含用户详细信息的映射
     *         - 若获取成功，Result 的 code 为 Code.SEARCH_OK，data 包含用户详细信息的映射。
     *         - 若获取失败，Result 的 code 为 Code.SEARCH_ERR，data 为错误信息字符串。
     */
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

    /**
     * 根据房屋ID列表统计各房屋类型的数量，并返回结果列表。
     *
     * @param houseIdList 包含房屋ID的列表
     * @return 结果列表，包含各房屋类型的名称和数量的映射
     *         - 每个映射包含两个键值对： "name" 表示房屋类型名称， "value" 表示该类型的数量。
     */
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

    /**
     * 检查是否存在相同手机号的账户，返回相应的结果对象。
     *
     * @param tele 待检查的手机号
     * @return 结果对象，包含检查结果和相关信息
     *         - 若不存在相同手机号的账户，Result 的 code 为 Code.SEARCH_OK，data 为注册令牌（AccountToken）。
     *         - 若存在相同手机号的账户，Result 的 code 为 Code.SAVE_ERR，data 为错误信息字符串（"500"）。
     */
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

    /**
     * 用户注册方法，将用户信息保存至数据库，并返回相应的结果对象。
     *
     * @param consumer 要注册的用户对象，包含必要的用户信息
     * @return 结果对象，包含注册成功或失败的状态和相关信息
     *         - 若注册成功，Result 的 code 为 Code.SAVE_OK，data 为 true。
     *         - 若注册失败，Result 的 code 为 Code.SAVE_ERR，data 为 false。
     *         - 若注册失败且手机号已存在，Result 的 code 为 Code.SAVE_ERR，data 为 false。
     */
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
        consumer.setMoney("99999999");
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

    /**
     * 用户登录方法，验证用户手机号和密码，并返回相应的结果对象。
     *
     * @param tele 用户手机号
     * @param pwd 用户密码
     * @return 结果对象，包含登录成功时的用户信息和令牌，或登录失败时的错误信息
     *         - 若登录成功，Result 的 code 为 Code.SEARCH_OK，data 包含 JSONObject，包括用户信息（"consumer"）、
     *           访问令牌（"accessToken"）和刷新令牌（"refreshToken"）。
     *         - 若登录失败，Result 的 code 为 Code.SEARCH_ERR，data 包含错误信息字符串。
     */
    @Override
    public Result userLogin(String tele,String pwd) {
        if (consumerDao.getPwdByTele(tele) != null && consumerDao.getPwdByTele(tele).equals(pwd)){
            //    获取用户信息
            Consumer consumer = consumerDao.getUserInfoByTele(tele);
            if(consumer.getConsumer_status() == 1){
                return new Result(Code.SEARCH_ERR,"当前用户被封禁");
            }
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

    /**
     * 用户自动登录方法，根据手机号获取用户信息，并生成访问令牌和刷新令牌返回结果对象。
     *
     * @param tele 用户手机号
     * @return 结果对象，包含自动登录成功时的用户信息和令牌
     *         - 若自动登录成功，Result 的 code 为 Code.SEARCH_OK，data 包含 JSONObject，包括用户信息（"consumer"）、
     *           访问令牌（"accessToken"）和刷新令牌（"refreshToken"）。
     *         - 若自动登录失败（手机号不存在等），Result 的 code 为 Code.SEARCH_ERR，data 包含错误信息字符串。
     *
     * @param tele 用户手机号
     */
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

    /**
     * 更新用户信息方法，根据提供的用户信息更新数据库中的用户数据。
     *
     * @param consumer 包含更新后的用户信息的 Consumer 对象
     * @return 结果对象，包含更新成功或失败的状态和相关信息
     *         - 若更新成功，Result 的 code 为 Code.UPDATE_OK，data 为更新成功的提示信息（"更新成功"）。
     *         - 若更新失败，Result 的 code 为 Code.UPDATE_ERR，data 为更新失败的提示信息（"更新失败"）。
     *
     * @param consumer 包含更新后的用户信息的 Consumer 对象
     */
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
