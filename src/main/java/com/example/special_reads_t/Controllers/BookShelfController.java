package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BookShelfController {

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;


    @GetMapping("/bookShelf")
    public String bookshelfTemplate(Model model, @RequestParam(defaultValue = "0") int page) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Page<JournalEntry> pageResult = journalService.getFinishedEntriesForUser(currentUser, page);
        List<JournalEntry> finishedBooks = pageResult.getContent();

        List<JournalEntry> row1 = new ArrayList<>();
        List<JournalEntry> row2 = new ArrayList<>();
        List<JournalEntry> row3 = new ArrayList<>();

        int totalSlots = 21;
        int slotsPerRow = 7;
        for (int i = 0; i < totalSlots; i++) {
            if (i < finishedBooks.size()) {
                if (i < slotsPerRow) {
                    row1.add(finishedBooks.get(i));
                } else if (i < slotsPerRow * 2) {
                    row2.add(finishedBooks.get(i));
                } else {
                    row3.add(finishedBooks.get(i));
                }
            } else {
                if (i < slotsPerRow) {
                    row1.add(null);
                } else if (i < slotsPerRow * 2) {
                    row2.add(null);
                } else {
                    row3.add(null);
                }
            }
        }


        model.addAttribute("row1", row1);
        model.addAttribute("row2", row2);
        model.addAttribute("row3", row3);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("hasNext", page < pageResult.getTotalPages() - 1);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        return "bookShelf";
    }
}
