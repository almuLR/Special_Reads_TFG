package com.example.special_reads_t.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/indexUser")
    public String indexUser() {
        return "indexUser";
    }

    @GetMapping("/login")
    public String loginTemplate() {
        return "login";
    }

    @GetMapping("/signUp")
    public String signUpTemplate() {
        return "signUp";
    }


}
