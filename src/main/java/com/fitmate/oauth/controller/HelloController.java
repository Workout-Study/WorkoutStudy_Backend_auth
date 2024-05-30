package com.fitmate.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/health_check")
    public String status() {
        return "auth-server-health";
    }
}
