package com.example.rental.domain.Role;

import lombok.Data;

@Data
public class Landlord {
    private int id;
    private String tele;
    private String pwd;
    private String landlord_name;
    private String introduce;
    private String qq;
    private String wechat;
    private String province;
    private String county;
    private int landlord_status;
    private String money;
    private int login_status;
    private String register_time;
    private String img_url;
    private String address;
}
