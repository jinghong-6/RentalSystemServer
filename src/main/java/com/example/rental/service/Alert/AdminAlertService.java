package com.example.rental.service.Alert;

import com.example.rental.domain.Alert.AdminAlert;
import com.example.rental.utils.Result;

public interface AdminAlertService {
    //  获取消息列表
    Result getAllAdminAlert();

    //  获取消息
    Result getAdminAlertById(String AlertId);

    //  更新消息状态
    Result updateConsumerAlertStatus(String AlertId);

    // 插入消息
    Result InsertAdminAlert(AdminAlert adminAlert);
}
