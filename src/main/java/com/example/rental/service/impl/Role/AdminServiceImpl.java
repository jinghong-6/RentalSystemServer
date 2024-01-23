package com.example.rental.service.impl.Role;

import com.alibaba.fastjson.JSONObject;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Role.AdminDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.domain.Role.Admin;
import com.example.rental.service.Role.AdminService;
import com.example.rental.utils.Code;
import com.example.rental.utils.JWTUtils;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private HouseDao houseDao;

    @Autowired
    private LandlordDao landlordDao;

    @Override
    public Result getAdminInfo(String Account) {
        Admin admin = adminDao.getAdminInfoByAccount(Account);

        if (admin != null){
            //    获取token
            String token = JWTUtils.getAdminToken("admin",admin.getAdmin_name());
            JSONObject json = new JSONObject();
            json.put("admin",admin);
            json.put("token",token);
            return new Result(Code.SEARCH_OK,json);
        }else {
            return new Result(Code.SEARCH_ERR,"未查询到相关信息");
        }
    }

    @Override
    public Result getAllInfo() {
        Map<String,Object> map = new HashMap<>();
        //  获取订单数
        String OrderNum = adminDao.getOrderNum().get("num").toString();
        //  获取用户量
        String ConsumerNum = adminDao.getConsumerNum().get("num").toString();
        //  获取商家量
        String LandlordNum = adminDao.getLandlordNum().get("num").toString();
        //  获取民宿量
        String HouseNum = adminDao.getHouseNum().get("num").toString();
        //  获取民宿类型
        Map<String, List<String>> HouseType = getCountLists(adminDao.getHouseId());
        //  获取每月订单数和总价
        List<Map<String, String>> OrderDateCountList = adminDao.getOrderDateCount();
        List<String> OrderMonth = new ArrayList<>();
        List<String> OrderCount = new ArrayList<>();
        List<String> AllPrice = new ArrayList<>();
        for (Map<String,String> orderDate : OrderDateCountList){
            OrderMonth.add(orderDate.get("month"));
            OrderCount.add(orderDate.get("count"));
            AllPrice.add(orderDate.get("total_price"));
        }
        map.put("OrderNum",OrderNum);
        map.put("ConsumerNum",ConsumerNum);
        map.put("LandlordNum",LandlordNum);
        map.put("HouseNum",HouseNum);
        map.put("HouseType",HouseType);
        map.put("OrderMonth",OrderMonth);
        map.put("OrderCount",OrderCount);
        map.put("AllPrice",AllPrice);
        return new Result(Code.SEARCH_OK,map);
    }

    @Override
    public Result getHouseByAdmin() {
        List<Map<String, Object>> HouseList = adminDao.getHouseByAdmin();
        if (HouseList != null){
            for (Map<String,Object> houseMap : HouseList){
                String LandlordName = landlordDao.getLandImgAndNameById(String.valueOf(houseMap.get("landlord_id"))).get("landlord_name");
                houseMap.put("LandlordName",LandlordName);
            }
            return new Result(Code.SEARCH_OK,HouseList);
        }else {
            return new Result(Code.SEARCH_ERR,"查找失败");
        }
    }

    @Override
    public Result UpdateHouseStatus(String status, String id) {
        if (status.equals("0")){
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 格式化日期
            String formattedDate = currentDate.format(formatter);
            List<String> overHouse = houseDao.getOverHouseList2(formattedDate,id);
            if (overHouse.size() == 0){
                boolean flag = houseDao.UpdateHouseStatusById(status,id);
                if (flag){
                    return new Result(Code.UPDATE_OK,"更新成功");
                }
            }
        }
        if (status.equals("2")){
            boolean flag = houseDao.UpdateHouseStatusById(status,id);
            if (flag){
                return new Result(Code.UPDATE_OK,"更新成功");
            }
        }
        return new Result(Code.UPDATE_ERR,"更新失败");
    }

    private Map<String, List<String>> getCountLists(List<String> houseIdList) {
        Map<String, List<String>> resultMap = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

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

        // 将计数结果放入 nameList 和 valueList
        for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
            nameList.add(entry.getKey());
            valueList.add(String.valueOf(entry.getValue()));
        }

        resultMap.put("name", nameList);
        resultMap.put("value", valueList);

        return resultMap;
    }

    @Override
    public Result AdminLogin(String Account, String pwd) {
        if (adminDao.getPwdByAccount(Account) != null && adminDao.getPwdByAccount(Account).equals(pwd)){
            Admin admin = adminDao.getAdminInfoByAccount(Account);

            if (admin != null){
                //    获取token
                String token = JWTUtils.getAdminToken("admin",admin.getAdmin_name());
                JSONObject json = new JSONObject();
                json.put("admin",admin);
                json.put("token",token);
                return new Result(Code.SEARCH_OK,json);
            }else {
                return new Result(Code.SEARCH_ERR,"登录失败");
            }
        }else {
            return new Result(Code.SEARCH_ERR,"登录失败");
        }
    }
}
