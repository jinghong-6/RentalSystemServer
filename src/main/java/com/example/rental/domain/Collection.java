package com.example.rental.domain;

import lombok.Data;

@Data
public class Collection {
    private String id;
    private String consumer_id;
    private String house_id;
    private String collection_time;
}