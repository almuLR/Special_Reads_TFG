package com.example.special_reads_t.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexUserController {

    @GetMapping("/bookShelf")
    public String bookShelfTemplate() {
        return "bookShelf";
    }
}
