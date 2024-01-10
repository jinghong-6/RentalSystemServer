package com.example.rental.service.impl.Alert;

import com.example.rental.dao.Alert.ConsumerAlertDao;
import com.example.rental.dao.HouseDao;
import com.example.rental.dao.Order.*;
import com.example.rental.domain.Alert.ConsumerAlert;
import com.example.rental.domain.House;
import com.example.rental.service.Alert.ConsumerAlertService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ConsumerAlertServiceImpl implements ConsumerAlertService {
    @Autowired
    ConsumerAlertDao consumerAlertDao;

    @Autowired
    OrderNopayDao orderNopayDao;

    @Autowired
    HouseDao houseDao;

    @Autowired
    OrderCompleteDao orderCompleteDao;

    @Autowired
    OrderCompletedDao orderCompletedDao;

    @Autowired
    OrderEndDao orderEndDao;

    @Autowired
    OrderBeginDao orderBeginDao;

    @Override
    /**
     * 根据消费者ID获取通知信息列表。
     *
     * @param consumerId 消费者ID
     * @return 包含通知信息的结果对象
     *         - 如果存在通知信息，则返回成功的结果对象(Result)，
     *           其中 Code 为 SEARCH_OK，数据为包含通知信息的列表(List<Map<String, String>>)
     *         - 如果没有找到通知信息，则返回失败的结果对象(Result)，
     *           其中 Code 为 SEARCH_ERR，消息为"暂无通知"
     */
    public Result getAlertByConsumerId(String ConsumerId) {
        List<Map<String, String>> AlertList = consumerAlertDao.getAlertListByConsumerId(ConsumerId);
        if (AlertList.size() == 0) {
            return new Result(Code.SEARCH_ERR, "暂无通知");
        } else {
            return new Result(Code.SEARCH_OK, AlertList);
        }
    }

    @Override
    /**
     * 根据消费者ID和通知ID获取通知信息。
     *
     * @param consumerId 消费者ID
     * @param alertId    通知ID
     * @return 包含通知信息的结果对象
     *         - 如果存在该通知信息，则返回成功的结果对象(Result)，
     *           其中 Code 为 SEARCH_OK，数据为包含通知信息的映射(Map<String, String>)
     *         - 如果未查询到相应通知信息，则返回失败的结果对象(Result)，
     *           其中 Code 为 SEARCH_ERR，消息为"未查询到"
     */
    public Result getAlertByConsumerIdAndAlertId(String ConsumerId, String AlertId) {
        Map<String, String> Alert = consumerAlertDao.getAlertByConsumerIdAndAlertId(ConsumerId, AlertId);
        if (Alert != null) {
            return new Result(Code.SEARCH_OK, Alert);
        } else {
            return new Result(Code.SEARCH_ERR, "未查询到");
        }
    }

    @Override
    /**
     * 更新消费者通知状态为已读。
     *
     * @param consumerId 消费者ID
     * @param alertId    通知ID
     * @return 包含更新结果的结果对象
     *         - 如果更新成功，则返回成功的结果对象(Result)，
     *           其中 Code 为 UPDATE_OK，消息为"已读"
     *         - 如果更新失败，则返回失败的结果对象(Result)，
     *           其中 Code 为 UPDATE_ERR，消息为"已读失败"
     */
    public Result updateConsumerAlertStatus(String ConsumerId, String AlertId) {
        if (consumerAlertDao.updateConsumerAlertStatus(ConsumerId, AlertId)) {
            return new Result(Code.UPDATE_OK, "已读");
        } else {
            return new Result(Code.UPDATE_ERR, "已读失败");
        }
    }

    @Override
    /**
     * 获取消费者的通知数量。
     *
     * @param consumerId 消费者ID
     * @return 包含通知数量的结果对象
     *         - 如果成功查询到通知数量，则返回成功的结果对象(Result)，
     *           其中 Code 为 SEARCH_OK，数据为通知数量(Integer)
     *         - 如果查询失败或未找到相应通知数量，则返回失败的结果对象(Result)，
     *           其中 Code 为 SEARCH_ERR，消息为"查询失败"
     */
    public Result getAlertCountByConsumerId(String ConsumerId) {
        Integer num = consumerAlertDao.getAlertCountByConsumerId(ConsumerId);
        if (num != null) {
            return new Result(Code.SEARCH_OK, num);
        } else {
            return new Result(Code.SEARCH_ERR, "查询失败");
        }
    }

    public void addConsumerAlert(String type, String uuid) {
        //支付成功的通知
        if (type.equals("0")) {
            Map<String, Object> Order = orderCompleteDao.getCompleteOrderByUuid(uuid);

            String HouseId = Order.get("house_id").toString();
            String ConsumerId = Order.get("consumer_id").toString();
            String OrderBeginTime = Order.get("begin_time").toString();
            String OrderEndTime = Order.get("end_time").toString();
            String OrderPrice = Order.get("price_all").toString();

            House house = houseDao.getHouseById(HouseId);

            ConsumerAlert consumerAlert = new ConsumerAlert();
            consumerAlert.setConsumer_id(ConsumerId);
            consumerAlert.setAlert_status("0");
            consumerAlert.setTitle("扣款通知");
            consumerAlert.setDatetime(getDateTime1());
            consumerAlert.setContent(
                    "您于" + getDateTime2() + "预定了" + house.getHouse_name() +
                            ",预定日期为" + OrderBeginTime + "至" + OrderEndTime + ",共支出" + OrderPrice + "元。"
            );
            consumerAlertDao.InsertConsumerAlert(consumerAlert);
        }
        //订单确认的通知
        if (type.equals("1")) {
            Map<String, Object> Order = orderCompletedDao.getCompletedOrderByUuid(uuid);

            String HouseId = Order.get("house_id").toString();
            String ConsumerId = Order.get("consumer_id").toString();
            String OrderBeginTime = Order.get("begin_time").toString();
            String OrderEndTime = Order.get("end_time").toString();

            House house = houseDao.getHouseById(HouseId);

            ConsumerAlert consumerAlert = new ConsumerAlert();
            consumerAlert.setConsumer_id(ConsumerId);
            consumerAlert.setAlert_status("0");
            consumerAlert.setTitle("商家接受订单通知");
            consumerAlert.setDatetime(getDateTime1());
            consumerAlert.setContent(
                    "您于" + getDateTime2() + "预定了" + house.getHouse_name() +
                            ",预定日期为" + OrderBeginTime + "至" + OrderEndTime +
                            ",订单已被房东接受，请耐心等待订单开始，祝您旅途愉快。"
            );
            consumerAlertDao.InsertConsumerAlert(consumerAlert);
        }
        //订单结束的通知
        if (type.equals("2")){
            Map<String, Object> Order = orderEndDao.getEndOrderByUuid(uuid);

            String HouseId = Order.get("house_id").toString();
            String ConsumerId = Order.get("consumer_id").toString();
            String OrderBeginTime = Order.get("begin_time").toString();
            String OrderEndTime = Order.get("end_time").toString();

            House house = houseDao.getHouseById(HouseId);

            ConsumerAlert consumerAlert = new ConsumerAlert();
            consumerAlert.setConsumer_id(ConsumerId);
            consumerAlert.setAlert_status("0");
            consumerAlert.setTitle("订单结束通知");
            consumerAlert.setDatetime(getDateTime1());
            consumerAlert.setContent(
                    "您于" + getDateTime2() + "预定的" + house.getHouse_name() +
                            ",预定日期为" + OrderBeginTime + "至" + OrderEndTime +
                            ",订单已于结束，希望您下次继续光临，若您满意的话，快去评价吧。"
            );
            consumerAlertDao.InsertConsumerAlert(consumerAlert);
        }
        //订单开始的通知
        if (type.equals("3")){
            Map<String, Object> Order = orderBeginDao.getBeginOrderByUuid(uuid);

            String HouseId = Order.get("house_id").toString();
            String ConsumerId = Order.get("consumer_id").toString();
            String OrderBeginTime = Order.get("begin_time").toString();
            String OrderEndTime = Order.get("end_time").toString();

            House house = houseDao.getHouseById(HouseId);

            ConsumerAlert consumerAlert = new ConsumerAlert();
            consumerAlert.setConsumer_id(ConsumerId);
            consumerAlert.setAlert_status("0");
            consumerAlert.setTitle("订单开始通知");
            consumerAlert.setDatetime(getDateTime1());
            consumerAlert.setContent(
                    "您于" + getDateTime2() + "预定的" + house.getHouse_name() +
                            ",预定日期为" + OrderBeginTime + "至" + OrderEndTime +
                            ",订单已开始，祝您旅途愉快，一路顺风。"
            );
            consumerAlertDao.InsertConsumerAlert(consumerAlert);
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
