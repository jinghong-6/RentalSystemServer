package com.example.rental.domain.Alert;

import lombok.Data;

@Data
public class AdminAlert {
    private String id;
    private String content;
    private String alert_status;
    private String user_id;
    private String user_type;
    private String create_time;
}
