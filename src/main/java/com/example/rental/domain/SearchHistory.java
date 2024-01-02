package com.example.rental.domain;

import lombok.Data;

@Data
public class SearchHistory {
    private String id;
    private String consumer_id;
    private String keyword;
    private String search_time;
}
