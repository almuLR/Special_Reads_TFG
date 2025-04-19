package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeneralController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/login")
    public String loginTemplate() {
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginErrorTemplate() { return "loginerror"; }

    @GetMapping("/logout")
    public String logoutTemplate() { return "logout"; }

    @GetMapping("/iaReader")
    public String iaReaderTemplate() {
        return "iaReader";
    }

    @GetMapping("/list")
    public String listTemplate() {
        return "list";
    }

    @GetMapping("/help")
    public String helpTemplate() {
        return "help";
    }
    @GetMapping("/helpAdmin")
    public String helpAdminTemplate() {
        return "helpAdmin";
    }

    @GetMapping("/bookSearchs")
    public String SearchsTemplate() {
        return "bookSearchs";
    }

    @GetMapping("/detailsBook")
    public String detailsBookTemplate() {
        return "bookDetailsView";
    }

    @GetMapping("/review")
    public String ReviewBookTemplate() {
        return "review";
    }
}
