package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.Challenge;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.ChallengeService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChallengeController {
    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private UserService userService;

    @GetMapping("/challenge")
    public String showChallenges(Model model) {
        User me = userService.getCurrentUser();
        List<Challenge> ch = challengeService.getChallengesFor(me);
        model.addAttribute("challenges", ch);
        return "challenge";
    }
}
