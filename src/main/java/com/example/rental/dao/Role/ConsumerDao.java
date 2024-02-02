package com.example.rental.dao.Role;

import com.example.rental.domain.Role.Consumer;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsumerDao {
    @Select("SELECT " +
            "COUNT(tele) > 0 as Num " +
            "FROM consumer " +
            "WHERE " +
            "tele = #{tele}")
    int getSameAccount(String tele);

    @Select("select consumer_name from consumer where id = #{consumerId}")
    String getConsumerNameById(String consumerId);

    @Select("select pwd from consumer where tele = #{tele}")
    String getPwdByTele(String tele);

    @Select("select " +
            "id,tele,consumer_name,qq,wechat,province,county,img_url,consumer_status,money,register_time,login_status,consumer_status " +
            "from consumer " +
            "where " +
            "tele = #{tele}")
    Consumer getUserInfoByTele(String tele);

    @Select("select consumer_name,img_url from consumer where id = #{id}")
    Map<String, String> getUserImgAndNameById(String id);

    @Select("select pay_pwd from consumer where id = #{id}")
    String getPayPwdById(String id);

    @Select("select pwd_lock_num from consumer where id = #{consumer_id}")
    int getPwdLockNumById(String id);

    @Select("select consumer_status from consumer where id = #{consumer_id}")
    int getConsumerStatus(String id);

    @Select("select money from consumer where id = #{consumer_id}")
    String getMoneyById(String consumer_id);

    @Insert("insert into consumer " +
            "values(" +
            "#{id},#{tele},#{pwd},#{pay_pwd},#{consumer_name},#{qq},#{wechat},#{province}," +
            "#{county},#{img_url},#{consumer_status},#{money},#{register_time},#{login_status},#{pwd_lock_num}" +
            ")")
    boolean setAccount(Consumer consumer);

    @Update("update consumer set pwd_lock_num = pwd_lock_num+1 where id = #{consumer_id}")
    void updatePayPwdLockNum(String consumer_id);

    @Update("update consumer set consumer_status = 1 where id = #{consumer_id}")
    boolean LockConsumer(String consumer_id);

    @Update("update consumer set consumer_status = 0 where id = #{consumer_id}")
    boolean ResetConsumerPayStatus(String consumer_id);

    @Update("update consumer set money = #{money} where id = #{consumer_id}")
    boolean UpdateConsumerMoney(@Param("consumer_id") String consumer_id, @Param("money") String money);

    @Update("update consumer set consumer_name = #{consumer_name},qq = #{qq},wechat = #{wechat},province = #{province},county = #{county},img_url = #{img_url} where id = #{id}")
    boolean UpdateConsumerInfo(Consumer consumer);
}
