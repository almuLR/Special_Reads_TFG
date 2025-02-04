package com.example.special_reads_t.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeneralController {

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/indexUser")
    public String indexUserTemplate() {
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

    @GetMapping("/iaReader")
    public String iaReaderTemplate() {
        return "iaReader";
    }

    @GetMapping("/journal")
    public String journalTemplate() {
        return "journal";
    }
    @GetMapping("/bookShelf")
    public String bookshelfTemplate() {
        return "bookShelf";
    }

    @GetMapping("/list")
    public String listTemplate() {
        return "list";
    }

    @GetMapping("/friends")
    public String friendsTemplate() {
        return "friends";
    }

    @GetMapping("/challenge")
    public String challengeTemplate() {
        return "challenge";
    }

    @GetMapping("/ranking")
    public String rankingTemplate() {
        return "ranking";
    }

    @GetMapping("/stats")
    public String statsTemplate() {
        return "stats";
    }

    @GetMapping("/profile")
    public String profileTemplate() {
        return "profile";
    }

    @GetMapping("/help")
    public String helpTemplate() {
        return "help";
    }

    @GetMapping("/bookSearchs")
    public String SearchsTemplate() {
        return "bookSearchs";
    }

    @GetMapping("/detailsBook")
    public String detailsBookTemplate() {
        return "detailsBook";
    }
    @GetMapping("/review")
    public String ReviewBookTemplate() {
        return "review";
    }

    @GetMapping("/profileEdit")
    public String profileEditTemplate() {
        return "profileEdit";
    }

    @GetMapping("/newRanking")
    public String newRankingTemplate() {
        return "newRanking";
    }

    @GetMapping("/editRanking")
    public String editRankingTemplate() {
        return "editRanking";
    }
}
