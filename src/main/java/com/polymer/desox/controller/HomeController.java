package com.polymer.desox.controller;

import com.polymer.desox.bean.ResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @PostMapping("/test")
    public ResponseBean test(){
        return ResponseBean.builder().status(HttpStatus.OK.value()).message("success").build();
    }

}
