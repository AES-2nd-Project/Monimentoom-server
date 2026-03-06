package com.example.monimentoom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cicd")
public class CICDController {
    @GetMapping("/test")
    public String cicdTest() {
        return "cicd 'version 1.0.0'";
    }

}
