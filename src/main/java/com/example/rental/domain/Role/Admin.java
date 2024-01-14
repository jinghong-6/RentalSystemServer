package com.example.rental.domain.Role;

import lombok.Data;

@Data
public class Admin {
    private String id;
    private String admin_name;
    private String admin_account;
    private String pwd;
    private String money;
}
