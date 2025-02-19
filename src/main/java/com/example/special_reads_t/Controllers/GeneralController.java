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


    @GetMapping("/indexUser")
    public String indexUserTemplate(Model model, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        model.addAttribute("userame", user.getUsername());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        return "indexUser";
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

    @GetMapping("/profile")
    public String profileTemplate() {
        return "profile";
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

    @GetMapping("/charts")
    public String chartsTemplate() {
        return "charts";
    }
}
