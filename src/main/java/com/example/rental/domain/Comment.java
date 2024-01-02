package com.example.rental.domain;

import lombok.Data;

@Data
public class Comment {
    private String id;
    private String parent_id;
    private String uuid;
    private String grades;
    private String content;
    private String user_id;
    private String user_type;
    private String imgs;
    private String create_time;
}
