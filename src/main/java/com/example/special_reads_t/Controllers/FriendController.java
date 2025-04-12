package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Service.FriendService;
import com.example.special_reads_t.Service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/friends")
    public String friendsTemplate() {



        return "friends";
    }

}
