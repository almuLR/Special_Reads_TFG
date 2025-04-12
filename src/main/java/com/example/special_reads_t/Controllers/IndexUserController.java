package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JournalService journalService;

    @GetMapping("/indexUser")
    public String indexUserTemplate(Model model, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username).orElseThrow();

        List<JournalEntry> currentlyReadingEntries = journalService.getReadingEntriesForUser(user);

        model.addAttribute("readingEntries", currentlyReadingEntries);
        model.addAttribute("userame", user.getUsername());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        return "indexUser";
    }
}
