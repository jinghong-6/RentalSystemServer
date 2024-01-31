package com.example.rental.domain.Alert;

import lombok.Data;

@Data
public class ConsumerAlert {
    private String id;
    private String consumer_id;
    private String title;
    private String content;
    private String alert_status;
    private String create_time;
}
