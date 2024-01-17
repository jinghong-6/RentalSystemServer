package com.example.rental.dao.Role;

import com.example.rental.domain.Role.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminDao {
    @Select("select pwd from admin where admin_account = #{Account}")
    String getPwdByAccount(String Account);

    @Select("select " +
            "id,admin_name,admin_account,money " +
            "from admin " +
            "where " +
            "admin_account = #{Account}")
    Admin getAdminInfoByAccount(String Account);

    @Update("update admin set money = #{money}")
    boolean updateAdminMoney(String money);


}
