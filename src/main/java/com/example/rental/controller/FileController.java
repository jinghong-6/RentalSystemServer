package com.example.rental.controller;

import com.example.rental.utils.fastdfs.FastDFSService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class FileController {
    @Resource
    private FastDFSService fastDFSService;

    @PostMapping("/file/upload")
    public String upload(MultipartFile file){
        return fastDFSService.uploadFile(file);
    }
}