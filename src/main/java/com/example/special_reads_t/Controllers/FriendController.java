package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.LeagueType;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.LeagueDto;
import com.example.special_reads_t.Service.FriendService;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.LeagueService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FriendController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private LeagueService leagueService;

    @GetMapping("/friends")
    public String friendsTemplate(Model model) {
        User currentUser = userService.getCurrentUser();

        List<LeagueDto> internalLeagues = leagueService.getInternalLeaguesDtos(currentUser);
        model.addAttribute("internalLeagues", internalLeagues);
        return "friends";
    }

}
