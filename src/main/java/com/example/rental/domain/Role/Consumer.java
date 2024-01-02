package com.example.rental.domain.Role;

import lombok.Data;

@Data
public class Consumer {
    private int id;
    private String tele;
    private String pwd;
    private String pay_pwd;
    private String consumer_name;
    private String qq;
    private String wechat;
    private String province;
    private String county;
    private String img_url;
    private int consumer_status;
    private String money;
    private String register_time;
    private int login_status;
    private int pwd_lock_num;
}
