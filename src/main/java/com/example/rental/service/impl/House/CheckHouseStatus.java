package com.example.rental.service.impl.House;

import com.example.rental.dao.HouseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CheckHouseStatus {
    @Autowired
    private HouseDao houseDao;

    @Scheduled(fixedRate = 60000)
    public void checkHouseStatus(){
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化日期
        String formattedDate = currentDate.format(formatter);

        //  设置已超时的民宿下架
        List<String> OverHouseId = houseDao.getOverHouseList(formattedDate);
        for (String id : OverHouseId){
            boolean flag = houseDao.UpdateHouseStatusById("2",id);
            if (flag){
                System.out.println("更新id为" + id + "的House状态成功，更新为2");
            }else {
                System.err.println("更新id为" + id + "的House状态失败");
            }
        }

        //  设置未超时但是被系统下架的民宿上架
        List<String> unOverHouseId = houseDao.getunOverHouseList(formattedDate);
        for (String id : unOverHouseId){
            boolean flag = houseDao.UpdateHouseStatusById("0",id);
            if (flag){
                System.out.println("更新id为" + id + "的House状态成功，更新为0");
            }else {
                System.err.println("更新id为" + id + "的House状态失败");
            }
        }
    }
}
