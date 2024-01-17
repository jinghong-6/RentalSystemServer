package com.example.rental.dao.Role;

import com.example.rental.domain.Role.Consumer;
import com.example.rental.domain.Role.Landlord;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface LandlordDao {
    @Select("SELECT " +
            "COUNT(tele) > 0 " +
            "as Num " +
            "FROM landlord " +
            "WHERE tele = #{tele}")
    public int getSameAccount(String tele);

    @Select("select pwd from landlord where tele = #{tele}")
    public String getPwdByTele(String tele);

    @Select("select " +
            "id,tele,landlord_name,introduce,qq,wechat,province,county,landlord_status,money,login_status,register_time,img_url,address " +
            "from landlord " +
            "where " +
            "tele = #{tele}")
    Landlord getLandInfoByTele(String tele);

    @Select("select landlord_name,img_url from landlord where id = #{id}")
    Map<String, String> getLandImgAndNameById(String id);

    @Select("select landlord_name,introduce,register_time,img_url,tele from landlord where id = #{id}")
    Map<String, String> getLandlordById(String id);

    @Select("select money from landlord where id = #{id}")
    Map<String, String> getLandMoneyById(String id);

    @Insert("insert into landlord " +
            "values(" +
            "#{id},#{tele},#{pwd},#{landlord_name},#{introduce},#{qq},#{wechat},#{province},#{county}," +
            "#{landlord_status},#{money},#{login_status},#{register_time},#{img_url},#{address}" +
            ")")
    public boolean setAccount(Landlord landlord);

    @Update("update landlord set landlord_name = #{landlord_name},qq = #{qq},wechat = #{wechat},province = #{province},county = #{county},img_url = #{img_url},introduce = #{introduce} where id = #{id}")
    boolean UpdateLandlordInfo(Landlord landlord);

    @Update("update landlord set money = #{money} where id = #{id}")
    boolean UpdateLandlordMoney(@Param("money") String money, @Param("id") String id);
}
