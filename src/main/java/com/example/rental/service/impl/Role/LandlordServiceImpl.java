package com.example.rental.service.impl.Role;

import com.alibaba.fastjson.JSONObject;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompleteDao;
import com.example.rental.dao.Order.OrderCompletedDao;
import com.example.rental.dao.Order.OrderEndDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.domain.Role.Landlord;
import com.example.rental.service.Role.LandlordService;
import com.example.rental.utils.CharacterFilter;
import com.example.rental.utils.Code;
import com.example.rental.utils.JWTUtils;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LandlordServiceImpl implements LandlordService {
    @Autowired
    private LandlordDao landlordDao;

    @Autowired
    private OrderEndDao orderEndDao;

    @Autowired
    private OrderCompleteDao orderCompleteDao;

    @Autowired
    private OrderCompletedDao orderCompletedDao;

    @Autowired
    private HouseDao houseDao;

    @Override
    /**
     * 获取房东相关信息，包括评论数、待确认订单数、已完成订单数、订单完成率、民宿类型统计以及每月订单数和总价。
     *
     * @param landlordId 房东ID
     * @return 包含房东相关信息的结果对象
     *         - 如果成功查询到相关信息，则返回成功的结果对象(Result)，
     *           其中 Code 为 SEARCH_OK，数据为包含房东信息的映射(Map<String, Object>)
     *         - 如果查询失败或未找到相关信息，则返回失败的结果对象(Result)，
     *           其中 Code 为 SEARCH_ERR
     */
    public Result getLandInfo(String LandlordId) {
        Map<String,Object> map = new HashMap<>();
        //  获取评论数
        List<Map<String, Object>> NotRatedOrders = orderEndDao.getNotRatedCommentOrderByLandlordId(LandlordId,"0");
        List<Map<String, Object>> RatedOrders = orderEndDao.getRatedCommentOrderByLandlordId(LandlordId);
        String NotRatedNum = String.valueOf(NotRatedOrders.size());
        String RatedNum = String.valueOf(RatedOrders.size());

        //  获取待确认订单
        List<Map<String, Object>> CompeleOrder = orderCompleteDao.getOrderByLandlordId(LandlordId);
        List<Map<String, Object>> CompeledOrder = orderCompletedDao.getOrderByLandlordId(LandlordId);
        String CompeleOrderNum = String.valueOf(CompeleOrder.size());
        String CompeledOrderNum = String.valueOf(CompeledOrder.size());

        //  获取订单完成率
        Map <String,String> OrderListNum = orderEndDao.getOrderEndNumByLandLordId(LandlordId);

        //  获取民宿类型
        List<Map<String, String>> MyHouse = getCountList(houseDao.getHouseIdByLandlordId(LandlordId));

        //  获取订单民宿类型
        List<Map<String, String>> OrderHouse = getCountList(orderEndDao.getHouseIdByLandLordId(LandlordId));

        //  获取每月订单数和总价
        List<Map<String, String>> OrderDateCountList = orderEndDao.getOrderDateCountByLandlordId(LandlordId);
        List<String> OrderMonth = new ArrayList<>();
        List<String> OrderCount = new ArrayList<>();
        List<String> AllPrice = new ArrayList<>();
        for (Map<String,String> orderDate : OrderDateCountList){
            OrderMonth.add(orderDate.get("month"));
            OrderCount.add(orderDate.get("count"));
            AllPrice.add(orderDate.get("total_price"));
        }

        map.putAll(OrderListNum);
        map.put("NotRatedNum",NotRatedNum);
        map.put("OrderHouseMap",OrderHouse);
        map.put("MyHouse",MyHouse);
        map.put("RatedNum",RatedNum);
        map.put("CompeleOrderNum",CompeleOrderNum);
        map.put("CompeledOrderNum",CompeledOrderNum);
        map.put("OrderMonth",OrderMonth);
        map.put("OrderCount",OrderCount);
        map.put("AllPrice",AllPrice);

        return new Result(Code.SEARCH_OK,map);
    }

    /**
     * 统计房屋类型数量并返回列表形式的结果。
     *
     * @param houseIdList 包含房屋ID的列表(List<String>)
     * @return 包含房屋类型统计结果的列表(List<Map<String, String>>)
     *         - 每个Map包含两个键值对："name"表示房屋类型，"value"表示该类型的数量
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

    @Override
    /**
     * 获取相同手机号的商家账户信息，用于商家注册时验证手机号唯一性。
     *
     * @param tele 手机号
     * @return 如果手机号在数据库中不存在相同账户，则返回成功的结果对象(Result)，
     *         其中 Code 为 SEARCH_OK，数据为注册令牌(AccountToken)；
     *         如果手机号在数据库中存在相同账户，则返回失败的结果对象(Result)，
     *         其中 Code 为 SAVE_ERR，数据为错误码"500"。
     */
    public Result getSameAccount(String tele) {
        System.out.println("商家注册");
        String filtered = CharacterFilter.filterSpecialCharacters(tele);
        if (landlordDao.getSameAccount(filtered) == 0){
            String AccountToken = JWTUtils.getRegisterToken(filtered,"land");
            return new Result(Code.SEARCH_OK,AccountToken);
        }else {
            return new Result(Code.SAVE_ERR,"500");
        }
    }

    @Override
    /**
     * 房东注册功能，将房东信息插入数据库进行注册。
     *
     * @param landlord 包含房东信息的对象(Landlord)
     * @return 如果成功注册房东账户，则返回成功的结果对象(Result)，
     *         其中 Code 为 SAVE_OK，数据为 true；
     *         如果注册失败，则返回失败的结果对象(Result)，
     *         其中 Code 为 SAVE_ERR，数据为 false。
     */
    public Result landlordRegister(Landlord landlord) {
        //  注册时间
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(date);

        landlord.setImg_url("http://zzw.fj.cn/p/group1/M00/00/00/CqgBbWX2vgOAWZxqAABvCRM88IY968.jpg");
        landlord.setLandlord_status(0);
        landlord.setRegister_time(time);
        landlord.setLogin_status(0);
        landlord.setMoney("0");

        if (landlordDao.setAccount(landlord)){
            return new Result(Code.SAVE_OK,true);
        }else {
            return new Result(Code.SAVE_ERR,false);
        }
    }

    /**
     * 房东登录方法，验证房东手机号和密码，并返回相应的结果对象。
     *
     * @param tele 房东手机号
     * @param pwd 房东密码
     * @return 结果对象，包含登录成功时的房东信息和令牌，或登录失败时的错误信息
     *         - 若登录成功，Result 的 code 为 Code.SEARCH_OK，data 包含 JSONObject，包括房东信息（"landlord"）、
     *           访问令牌（"accessToken"）和刷新令牌（"refreshToken"）。
     *         - 若登录失败，Result 的 code 为 Code.SEARCH_ERR，data 包含错误信息字符串（"500"）。
     */
    @Override
    public Result landlordLogin(String tele, String pwd) {
        if (landlordDao.getPwdByTele(tele) != null && landlordDao.getPwdByTele(tele).equals(pwd)){
            //    获取用户信息
            Landlord landlord = landlordDao.getLandInfoByTele(tele);
            if (landlord.getLandlord_status() == 1){
                return new Result(Code.SEARCH_ERR,"当前用户被封禁");
            }
            //    获取token
            String accessToken = JWTUtils.getLoginAccessToken(landlord.getTele(),"landlord",landlord.getLandlord_name());
            String refreshToken = JWTUtils.getLoginRefreshToken(landlord.getTele(),"landlord",landlord.getLandlord_name());

            JSONObject json = new JSONObject();
            json.put("landlord",landlord);
            json.put("accessToken",accessToken);
            json.put("refreshToken",refreshToken);

            return new Result(Code.SEARCH_OK,json);
        }else {
            return new Result(Code.SEARCH_ERR,500);
        }
    }

    @Override
    /**
     * 房东登录功能，验证手机号和密码，返回登录结果和相应的访问令牌。
     *
     * @param tele 手机号
     * @param pwd  密码
     * @return 如果手机号和密码验证成功，则返回成功的结果对象(Result)，
     *         其中 Code 为 SEARCH_OK，数据为包含房东信息和令牌的JSON对象(JSONObject)；
     *         如果验证失败，则返回失败的结果对象(Result)，
     *         其中 Code 为 SEARCH_ERR，数据为错误码"500"。
     */
    public Result landlordAutoLogin(String tele) {
        //    获取用户信息
        Landlord landlord = landlordDao.getLandInfoByTele(tele);
        System.out.println(landlord);
        //    获取token
        String accessToken = JWTUtils.getLoginAccessToken(landlord.getTele(),"landlord",landlord.getLandlord_name());
        String refreshToken = JWTUtils.getLoginRefreshToken(landlord.getTele(),"landlord",landlord.getLandlord_name());

        JSONObject json = new JSONObject();
        json.put("landlord",landlord);
        json.put("accessToken",accessToken);
        json.put("refreshToken",refreshToken);

        return new Result(Code.SEARCH_OK,json);
    }

    @Override
    /**
     * 更新房东信息。
     *
     * @param landlord 包含更新后房东信息的对象(Landlord)
     * @return 如果成功更新房东信息，则返回成功的结果对象(Result)，
     *         其中 Code 为 UPDATE_OK，消息为"更新成功"；
     *         如果更新失败，则返回失败的结果对象(Result)，
     *         其中 Code 为 UPDATE_ERR，消息为"更新失败"。
     */
    public Result UpdateLandlordInfo(Landlord landlord) {
        System.out.println(landlord);
        boolean updateResult = landlordDao.UpdateLandlordInfo(landlord);
        if (updateResult){
            return new Result(Code.UPDATE_OK,"更新成功");
        }else {
            return new Result(Code.UPDATE_ERR,"更新失败");
        }
    }
}
