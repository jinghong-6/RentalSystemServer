package com.example.rental.controller;

import com.example.rental.utils.Code;
import com.example.rental.utils.JWTUtils;
import com.example.rental.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setToken")
public class setTokenController {
    @PostMapping()
    public Result getToken(String tele, String UT, String Name){
        if (UT.equals("jinghong001")){
            String accessToken = JWTUtils.getLoginAccessToken(tele,"consumer",Name);
            String refreshToken = JWTUtils.getLoginRefreshToken(tele,"consumer",Name);
            List<String> tokens = new ArrayList<>();
            tokens.add(accessToken);
            tokens.add(refreshToken);
            return new Result(Code.UPDATE_OK,tokens);
        }else if (UT.equals("jinghong002")){
            String accessToken = JWTUtils.getLoginAccessToken(tele,"landlord",Name);
            String refreshToken = JWTUtils.getLoginRefreshToken(tele,"landlord",Name);
            List<String> tokens = new ArrayList<>();
            tokens.add(accessToken);
            tokens.add(refreshToken);
            return new Result(Code.UPDATE_OK,tokens);
        }else {
            return new Result(Code.UPDATE_ERR,500);
        }
    }
}
