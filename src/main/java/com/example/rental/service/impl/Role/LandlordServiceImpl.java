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
    public Result landlordRegister(Landlord landlord) {
        //  注册时间
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(date);

        landlord.setImg_url("http://10.168.1.160/api/group1/M00/00/00/CqgBuWTEwIiAGdpFAABvCRM88IY897.jpg");
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

    @Override
    public Result landlordLogin(String tele, String pwd) {
        if (landlordDao.getPwdByTele(tele).equals(pwd)){
            //    获取用户信息
            Landlord landlord = landlordDao.getLandInfoByTele(tele);
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
