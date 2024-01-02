package com.example.rental.domain;

import lombok.Data;

@Data
public class House {
    private String id;
    private String landlord_id;
    private String house_name;
    private String introduce;
    private String price;
    private String city_id;
    private String full_address;
    private String firstImg;
    private String img;
    private String begin_time;
    private String end_time;
    private String service;
    private String type;
    private String max_num;
    private String house_status;
}