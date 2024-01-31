package com.example.rental.service.impl.Alert;

import com.example.rental.dao.Alert.LandlordAlertDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.OrderCompleteDao;
import com.example.rental.dao.Order.OrderEndDao;
import com.example.rental.dao.Order.OrderNopayDao;
import com.example.rental.domain.Alert.LandlordAlert;
import com.example.rental.domain.House;
import com.example.rental.service.Alert.LandlordAlertService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LandlordAlertServiceImpl implements LandlordAlertService {
    @Autowired
    LandlordAlertDao landlordAlertDao;

    @Autowired
    OrderNopayDao orderNopayDao;

    @Autowired
    HouseDao houseDao;

    @Autowired
    OrderCompleteDao orderCompleteDao;

    @Autowired
    OrderEndDao orderEndDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据房东ID获取通知信息。
     *
     * @param LandlordId 房东ID
     * @return 返回包含通知信息的Result对象。如果没有通知，返回Code.SEARCH_ERR和"暂无通知"；否则，返回Code.SEARCH_OK和通知列表。
     */
    @Override
    public Result getAlertByLandlordId(String LandlordId) {
        String LandlordKey = "landlord" + LandlordId;
        Object data = redisTemplate.opsForValue().get(LandlordKey);
        if (data != null){
            return new Result(Code.SEARCH_OK, data);
        }else {
            List<Map<String, String>> AlertList = landlordAlertDao.getAlertListByLandlordId(LandlordId);
            if (AlertList.size() == 0) {
                return new Result(Code.SEARCH_ERR, "暂无通知");
            } else {
                redisTemplate.opsForValue().set(LandlordKey, AlertList);
                redisTemplate.expire(LandlordKey, 60, TimeUnit.SECONDS); // 设置过期时间为60秒
                return new Result(Code.SEARCH_OK, AlertList);
            }
        }
    }

    /**
     * 根据房东ID和通知ID获取单条通知信息。
     *
     * @param LandlordId 房东ID
     * @param AlertId    通知ID
     * @return 返回包含单条通知信息的Result对象。如果查询到通知，返回Code.SEARCH_OK和通知信息；否则，返回Code.SEARCH_ERR和"未查询到"。
     */
    @Override
    public Result getAlertByLandlordIdAndAlertId(String LandlordId, String AlertId) {
        Map<String, String> Alert = landlordAlertDao.getAlertByLandlordIdAndAlertId(LandlordId, AlertId);
        if (Alert != null) {
            return new Result(Code.SEARCH_OK, Alert);
        } else {
            return new Result(Code.SEARCH_ERR, "未查询到");
        }
    }

    /**
     * 更新房东通知状态，将指定通知标记为已读。
     *
     * @param LandlordId 房东ID
     * @param AlertId    通知ID
     * @return 返回包含更新结果的Result对象。如果更新成功，返回Code.UPDATE_OK和"已读"；否则，返回Code.UPDATE_ERR和"已读失败"。
     */
    @Override
    public Result updateLandlordAlertStatus(String LandlordId, String AlertId) {
        if (landlordAlertDao.updateLandlordAlertStatus(LandlordId, AlertId)) {
            return new Result(Code.UPDATE_OK, "已读");
        } else {
            return new Result(Code.UPDATE_ERR, "已读失败");
        }
    }

    /**
     * 获取房东通知数量。
     *
     * @param LandlordId 房东ID
     * @return 返回包含房东通知数量的Result对象。如果查询成功，返回Code.SEARCH_OK和通知数量；否则，返回Code.SEARCH_ERR和"查询失败"。
     */
    @Override
    public Result getAlertCountByLandlordId(String LandlordId) {
        Integer num = landlordAlertDao.getAlertCountByLandlordId(LandlordId);
        if (num != null) {
            return new Result(Code.SEARCH_OK, num);
        } else {
            return new Result(Code.SEARCH_ERR, "查询失败");
        }
    }

    /**
     * 添加房东通知。
     *
     * @param type 通知类型，"0"表示新增订单通知，"1"表示新增评论通知。
     * @param uuid 订单或评论的唯一标识符。
     */
    public void addLandlordAlert(String type,String uuid){
        //商家新增订单通知
        if (type.equals("0")){
            Map<String, Object> Order = orderNopayDao.getNopayOrderByUuid(uuid);
            if (Order == null){
                Order = orderCompleteDao.getCompleteOrderByUuid(uuid);
            }

            String HouseId = Order.get("house_id").toString();
            String LandlordId = Order.get("landlord_id").toString();
            String OrderBeginTime = Order.get("begin_time").toString();
            String OrderEndTime = Order.get("end_time").toString();

            House house = houseDao.getHouseById(HouseId);

            LandlordAlert landlordAlert = new LandlordAlert();
            landlordAlert.setLandlord_id(LandlordId);
            landlordAlert.setAlert_status("0");
            landlordAlert.setTitle("新增订单通知");
            landlordAlert.setCreate_time(getDateTime1());
            landlordAlert.setContent(
                    "您的" + house.getHouse_name() + "于" + getDateTime1() + "被预定了" +
                            ",预定日期为" + OrderBeginTime + "至" + OrderEndTime + ",快去确认吧。"
            );
            landlordAlertDao.InsertLandlordAlert(landlordAlert);
            redisTemplate.delete("landlord" + landlordAlert.getLandlord_id());
        }
        //商家新增评论通知
        if (type.equals("1")){
            Map<String, Object> Order = orderEndDao.getEndOrderByUuid(uuid);

            String HouseId = Order.get("house_id").toString();
            String LandlordId = Order.get("landlord_id").toString();

            House house = houseDao.getHouseById(HouseId);

            LandlordAlert landlordAlert = new LandlordAlert();
            landlordAlert.setLandlord_id(LandlordId);
            landlordAlert.setAlert_status("0");
            landlordAlert.setTitle("评论通知");
            landlordAlert.setCreate_time(getDateTime1());
            landlordAlert.setContent(
                    "您的" + house.getHouse_name() + "于" + getDateTime1() + "被评论了" +
                            ",快去查看吧。"
            );
            landlordAlertDao.InsertLandlordAlert(landlordAlert);
            redisTemplate.delete("landlord" + landlordAlert.getLandlord_id());
        }
    }

    private String getDateTime1() {
        // 创建 SimpleDateFormat 对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        return sdf.format(new Date());
    }

    private String getDateTime2() {
        // 创建 SimpleDateFormat 对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前时间
        return sdf.format(new Date());
    }
}
