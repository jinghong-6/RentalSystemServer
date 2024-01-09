package com.example.rental.domain.Alert;

import lombok.Data;

@Data
public class LandlordAlert {
    private String id;
    private String landlord_id;
    private String title;
    private String content;
    private String alert_status;
    private String datetime;
}
