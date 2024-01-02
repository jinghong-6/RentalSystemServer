package com.example.rental.domain.Order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderEnd {
    private String id;
    private String uuid;
    private String order_id;
    private String consumer_id;
    private String landlord_id;
    private String price_all;
    private String house_id;
    private String price;
    private String begin_time;
    private String end_time;
    private String people_num;
    private LocalDateTime order_begin_time;
    private LocalDateTime order_pay_time;
    private String order_status;
    private String grades;
}
