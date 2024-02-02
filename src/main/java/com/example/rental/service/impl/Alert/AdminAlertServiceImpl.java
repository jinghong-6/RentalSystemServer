package com.example.rental.service.impl.Alert;

import com.example.rental.dao.Alert.AdminAlertDao;
import com.example.rental.dao.Role.ConsumerDao;
import com.example.rental.dao.Role.LandlordDao;
import com.example.rental.domain.Alert.AdminAlert;
import com.example.rental.service.Alert.AdminAlertService;
import com.example.rental.utils.Code;
import com.example.rental.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminAlertServiceImpl implements AdminAlertService {
    @Autowired
    private AdminAlertDao adminAlertDao;

    @Autowired
    private ConsumerDao consumerDao;

    @Autowired
    private LandlordDao landlordDao;

    @Override
    public Result getAllAdminAlert() {
        List<AdminAlert> adminAlerts = adminAlertDao.getAllAdminAlert();
        if (adminAlerts != null) {
            List<Map<String,Object>> resultMap = new ArrayList<>();
            for (AdminAlert adminAlert : adminAlerts) {
                resultMap.add(getObjectFields(adminAlert));
            }
            return new Result(Code.SEARCH_OK, resultMap);
        } else {
            return new Result(Code.SEARCH_ERR, "查询信息失败");
        }
    }

    @Override
    public Result getAdminAlertById(String AlertId) {
        AdminAlert adminAlert = adminAlertDao.getAdminAlertById(AlertId);
        if (adminAlert != null) {
            Map<String, Object> map = getObjectFields(adminAlert);
            return new Result(Code.SEARCH_OK, map);
        } else {
            return new Result(Code.SEARCH_ERR, "查询信息失败");
        }
    }

    @Override
    public Result updateConsumerAlertStatus(String AlertId) {
        boolean result = adminAlertDao.updateConsumerAlertStatus(AlertId);
        if (result) {
            return new Result(Code.UPDATE_OK, "已读");
        } else {
            return new Result(Code.UPDATE_OK, "已读失败");
        }
    }

    @Override
    public Result InsertAdminAlert(AdminAlert adminAlert) {
        // 获取当前日期
        Date currentDate = new Date();
        // 创建SimpleDateFormat对象，指定日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 使用SimpleDateFormat格式化日期
        String formattedDate = dateFormat.format(currentDate);

        adminAlert.setCreate_time(formattedDate);
        boolean result = adminAlertDao.InsertAdminAlert(adminAlert);
        if (result) {
            return new Result(Code.UPDATE_OK, "新增成功");
        } else {
            return new Result(Code.UPDATE_OK, "新增失败");
        }
    }

    public Map<String, Object> getObjectFields(AdminAlert adminAlert) {
        Map<String, Object> map = new HashMap<>();

        // 获取对象的所有字段
        Field[] fields = adminAlert.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                // 设置字段可访问，以便获取私有字段的值
                field.setAccessible(true);
                // 获取字段的名称
                String fieldName = field.getName();
                // 当字段名称为 "user_id" 时
                if ("user_id".equals(fieldName)) {
                    //  1是代表用户，2是代表商家
                    if (adminAlert.getUser_type().equals("1")) {
                        String name = consumerDao.getConsumerNameById(adminAlert.getUser_id());
                        if (name != null) {
                            map.put("userName", name);
                        }
                    }
                    if (adminAlert.getUser_type().equals("2")) {
                        String name = landlordDao.getLandlordNameById(adminAlert.getUser_id());
                        if (name != null) {
                            map.put("userName", name);
                        }
                    }
                } else {
                    // 获取字段的值，并放入Map中
                    map.put(fieldName, field.get(adminAlert));
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
