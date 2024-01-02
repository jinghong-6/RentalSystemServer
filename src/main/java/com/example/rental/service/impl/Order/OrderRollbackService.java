package com.example.rental.service.impl.Order;

import com.example.rental.dao.Order.*;
import com.example.rental.dao.Role.ConsumerDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@Service
public class OrderRollbackService {
    @Autowired
    private ConsumerDao consumerDao;

    @Autowired
    private OrderNopayDao orderNopayDao;

    @Autowired
    private OrderCompleteDao orderCompleteDao;

    @Autowired
    private OrderCompletedDao orderCompletedDao;

    @Autowired
    private OrderBeginDao orderBeginDao;

    @Autowired
    private OrderEndDao orderEndDao;

    @Autowired
    private LandlordDao landlordDao;

    //  未支付订单超时归档的事务操作
    @Transactional(rollbackFor = Exception.class)// 在发生任何异常时回滚事务
    public boolean moveOrderNopayToOrderEndAndFromOrderNopay(String uuid) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            // 定义日期时间格式化器，用于解析目标时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 在同一个事务中执行三个操作
            boolean changeOrderStatusResult = orderNopayDao.updateOrderStatusToEnd(uuid);
            boolean moveResult = orderEndDao.moveOrderNopayToOrderEnd(uuid);
            boolean deleteResult = orderNopayDao.deleteDataFromOrderNopay(uuid);
            boolean EndTimeResult = orderEndDao.updateOrderCloseTime(currentTime.format(formatter),uuid);

            // 根据需要处理结果
            if (moveResult && deleteResult && changeOrderStatusResult && EndTimeResult) {
                // 操作成功
                System.out.println("未支付订单超时归档成功");
                return true;
            } else {
                // 操作失败，手动抛出异常触发回滚
                throw new RuntimeException("操作失败，触发回滚");
            }
        } catch (Exception e) {
            // 处理异常，如果需要的话
            throw new RuntimeException("操作失败，触发回滚");
        }
    }

    //  待确认订单超时归档的事务操作
    @Transactional(rollbackFor = Exception.class)// 在发生任何异常时回滚事务
    public boolean moveOrderCompleteToOrderEndFromOrderComplete(String uuid) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            // 定义日期时间格式化器，用于解析目标时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 在同一个事务中执行三个操作
            boolean changeOrderStatusResult = orderCompleteDao.updateOrderStatusToEnd(uuid);
            boolean moveResult = orderEndDao.moveCompleteToOrderEnd(uuid);
            boolean deleteResult = orderCompleteDao.deleteDataFromOrderComplete(uuid);
            boolean EndTimeResult = orderEndDao.updateOrderCloseTime(currentTime.format(formatter),uuid);

            // 根据需要处理结果
            if (moveResult && deleteResult && changeOrderStatusResult && EndTimeResult) {
                // 操作成功
                System.out.println("待确认订单超时归档成功");
                return true;
            } else {
                // 操作失败，手动抛出异常触发回滚
                throw new RuntimeException("操作失败，触发回滚");
            }
        } catch (Exception e) {
            // 处理异常，如果需要的话
            throw new RuntimeException("操作失败，触发回滚");
//            return false;
        }
    }

    //  待开始订单转化为开始订单的事务操作
    @Transactional(rollbackFor = Exception.class)// 在发生任何异常时回滚事务
    public boolean moveOrderCompletedToOrderBeginFromOrderCompleted(String uuid) {
        try {
            // 在同一个事务中执行三个操作
            boolean moveResult = orderBeginDao.moveOrderCompletedToOrderBegin(uuid);
            boolean deleteResult = orderCompletedDao.deleteDataFromOrderCompleted(uuid);

            // 根据需要处理结果
            if (moveResult && deleteResult) {
                // 操作成功
                System.out.println("待开始订单转化为开始订单成功");
                return true;
            } else {
                // 操作失败，手动抛出异常触发回滚
                throw new RuntimeException("操作失败，触发回滚");
            }
        } catch (Exception e) {
            // 处理异常，如果需要的话
            throw new RuntimeException("操作失败，触发回滚");
//            return false;
        }
    }

    //  将未支付订单改变为待确认的事务操作
    @Transactional
    public Result moveDataToOrderCompleteAndDeleteDataFromOrderNopay(String uuid,String consumerId,String landlordId,Long newMoney,Long OrderPrice) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            // 定义日期时间格式化器，用于解析目标时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 创建Calendar实例
            Calendar calendar = Calendar.getInstance();
            // 将当前时间加上一天
            calendar.add(Calendar.DATE, 1);
            // 格式化输出时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 在同一个事务中执行多个操作
            boolean moveResult = orderCompleteDao.moveDataToOrderComplete(uuid);
            boolean deleteResult = orderNopayDao.deleteDataFromOrderNopay(uuid);
            boolean updateTimeResult = orderCompleteDao.updateOrderPayTimeAndEndTime(currentTime.format(formatter),sdf.format(calendar.getTime()),uuid);
            //  扣款
            boolean consumerMoneyResult = consumerDao.UpdateConsumerMoney(consumerId, String.valueOf(newMoney));
            //  更新支付状态
            boolean consumerPayStatusResult = consumerDao.ResetConsumerPayStatus(consumerId);
            //  商家收款
            String nowLandMoney = landlordDao.getLandMoneyById(landlordId).get("money");
            Long newLandMoney = Long.parseLong(nowLandMoney) + OrderPrice;
            boolean landlordGetMoneyResult = landlordDao.UpdateLandlordMoney(String.valueOf(newLandMoney),landlordId);
            String nowLandMoney2 = landlordDao.getLandMoneyById(landlordId).get("money");
            System.out.println("nowLandMoney2" + nowLandMoney2);
            // 根据需要处理结果
            if (moveResult && deleteResult && updateTimeResult && consumerMoneyResult && consumerPayStatusResult && landlordGetMoneyResult) {
                // 操作成功
                return new Result(Code.SAVE_ERR, "订单状态更改成功");
            } else {
                // 操作失败，手动抛出异常触发回滚
                throw new RuntimeException("操作失败，触发回滚");
            }
        } catch (Exception e) {
            // 处理异常，如果需要的话
            return new Result(Code.SAVE_ERR, "订单状态更改失败");
        }
    }

    //  将待确认订单改变为未开始的事务操作
    @Transactional
    public Result moveCompleteToCompletedFromOrderComplete(String uuid) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            // 定义日期时间格式化器，用于解析目标时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 在同一个事务中执行两个操作
            boolean moveResult = orderCompletedDao.moveOrderCompleteToOrderCompleted(uuid);
            boolean deleteResult = orderCompleteDao.deleteDataFromOrderComplete(uuid);
            boolean updateConfirmResult = orderCompletedDao.updateOrderConfirmTime(currentTime.format(formatter),uuid);

            // 根据需要处理结果
            if (moveResult && deleteResult && updateConfirmResult) {
                // 操作成功
                return new Result(Code.UPDATE_OK, "订单状态更改成功");
            } else {
                // 操作失败，手动抛出异常触发回滚
                throw new RuntimeException("操作失败，触发回滚");
            }
        } catch (Exception e) {
            // 处理异常，如果需要的话
            return new Result(Code.SAVE_ERR, "订单状态更改失败");
        }
    }

    //  订单正常结束的事务操作
    @Transactional
    public boolean moveBeginToEndFromOrderBegin(String uuid) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            // 定义日期时间格式化器，用于解析目标时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 在同一个事务中执行两个操作
            boolean moveResult = orderEndDao.moveBeginToOrderEnd(uuid);
            boolean deleteResult = orderBeginDao.deleteDataFromOrderBegin(uuid);
            boolean updateConfirmResult = orderEndDao.updateOrderCloseTime(currentTime.format(formatter),uuid);

            // 根据需要处理结果
            if (moveResult && deleteResult && updateConfirmResult) {
                // 操作成功
                System.out.println("开始订单归档成功");
                return true;
            } else {
                // 操作失败，手动抛出异常触发回滚
                throw new RuntimeException("操作失败，触发回滚");
            }
        } catch (Exception e) {
            // 处理异常，如果需要的话
            // 操作失败，手动抛出异常触发回滚
            throw new RuntimeException("操作失败，触发回滚");
        }
    }
}
