package com.example.rental.service.impl.Order;

import com.example.rental.dao.Order.*;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class checkAndProcessExpiredOrders {
    @Autowired
    private OrderNopayDao orderNopayDao;

    @Autowired
    private OrderCompleteDao orderCompleteDao;

    @Autowired
    private OrderCompletedDao orderCompletedDao;

    @Autowired
    private OrderBeginDao orderBeginDao;

    @Autowired
    private OrderRollbackService orderRollbackService;

    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkAndProcessExpiredOrders(){

        LocalDateTime currentTime = LocalDateTime.now();
        // 定义日期时间格式化器，用于解析目标时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<String> processExpiredNopayOrders = orderNopayDao.getAllProcessExpiredOrdersByDateTime(currentTime.format(formatter));
        for (String uuid : processExpiredNopayOrders){
            try {
                boolean NoPaySuccess = orderRollbackService.moveOrderNopayToOrderEndAndFromOrderNopay(uuid);
                if (!NoPaySuccess) {
                    // 处理操作失败的情况，例如记录错误日志
                    System.err.println("订单超时处理失败: " + uuid);
                }
                System.out.println("订单超时处理成功: " + uuid);
            } catch (MyBatisSystemException e) {
                // 处理MyBatis异常
                System.err.println("MyBatis异常: " + e.getMessage());
            }
        }

        List<String> processExpiredCompleteOrders = orderCompleteDao.getAllProcessExpiredOrdersByDateTime(currentTime.format(formatter));
        for (String uuid : processExpiredCompleteOrders){
            try {
                boolean CompleteSuccess = orderRollbackService.moveOrderCompleteToOrderEndFromOrderComplete(uuid);
                if (!CompleteSuccess) {
                    // 处理操作失败的情况，例如记录错误日志
                    System.err.println("订单超时处理失败: " + uuid);
                }
            } catch (MyBatisSystemException e) {
                // 处理MyBatis异常
                System.err.println("MyBatis异常: " + e.getMessage());
            }
        }

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> processExpiredCompletedOrders = orderCompletedDao.getAllProcessExpiredOrdersByDate(currentTime.format(formatter2));
        for (String uuid : processExpiredCompletedOrders){
            try {
                boolean CompletedSuccess = orderRollbackService.moveOrderCompletedToOrderBeginFromOrderCompleted(uuid);
                if (!CompletedSuccess) {
                    // 处理操作失败的情况，例如记录错误日志
                    System.err.println("订单超时处理失败: " + uuid);
                }
            } catch (MyBatisSystemException e) {
                // 处理MyBatis异常
                System.err.println("MyBatis异常: " + e.getMessage());
            }
        }

        List<String> processExpiredBeginOrders = orderBeginDao.getAllProcessExpiredOrdersByDate(currentTime.format(formatter2));
        for (String uuid : processExpiredBeginOrders){
            try {
                boolean EndSuccess = orderRollbackService.moveBeginToEndFromOrderBegin(uuid);
                if (!EndSuccess) {
                    // 处理操作失败的情况，例如记录错误日志
                    System.err.println("订单超时处理失败: " + uuid);
                }
            } catch (MyBatisSystemException e) {
                // 处理MyBatis异常
                System.err.println("MyBatis异常: " + e.getMessage());
            }
        }
    }
}
