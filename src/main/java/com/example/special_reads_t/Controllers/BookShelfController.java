package com.example.special_reads_t.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookShelfController {
    @GetMapping("/bookShelf")
    public String bookshelfTemplate() {
        return "bookShelf";
    }
}
