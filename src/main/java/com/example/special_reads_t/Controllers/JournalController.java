package com.example.special_reads_t.Controllers;


import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.UserService;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller

public class JournalController {

    @Autowired
    private BookService bookService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;


    @PostMapping("/journal/add/{googleBookId}")
    public String addBookToJournal(@PathVariable("googleBookId") String googleBookId) {
        Book book = bookService.detailsBook(googleBookId);
        if (book != null) {
            JournalEntry entry = journalService.addBookToJournal(book);
            return "redirect:/journal?entryId=" + entry.getId();
        }
        return "redirect:/journal";
    }

    @PostMapping("/journal/updateProgress")
    public String updateProgress(@RequestParam("journalEntryId") Long journalEntryId, @RequestParam("progressType") String progressType,
                                 @RequestParam("progressValue") int progressValue, @RequestParam("status") String status) {

        JournalEntry entry = journalService.findById(journalEntryId);
        if (entry != null) {
            if ("pages".equalsIgnoreCase(progressType)) {
                int totalPages = entry.getBook().getPageCount();
                if (totalPages > 0) {
                    int percentage = (int) ((progressValue / (double)totalPages) * 100);
                    entry.setProgress(percentage);
                }
            } else if ("percentage".equalsIgnoreCase(progressType)) {
                entry.setProgress(progressValue);
            }
            entry.setStatus(status);
            journalService.updateJournalEntry(entry);
        }
        return "redirect:/journal";
    }

    @GetMapping("/journal")
    public String showjournal(Model model, @RequestParam(defaultValue = "0") int page) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Page<JournalEntry> pageResult = journalService.getAllEntriesForUser(currentUser, page);
        List<JournalEntry> entries = pageResult.getContent();


        List<JournalEntry> leftJournalEntries = new ArrayList<>();
        List<JournalEntry> rightJournalEntries = new ArrayList<>();


        int slotsPerSide = 9;
        for (int i = 0; i < slotsPerSide && i < entries.size(); i++) {
            leftJournalEntries.add(entries.get(i));
        }
        for (int i = slotsPerSide; i < 2 * slotsPerSide && i < entries.size(); i++) {
            rightJournalEntries.add(entries.get(i));
        }
        for (JournalEntry entry : entries) {
            if ("Pendiente".equalsIgnoreCase(entry.getStatus())) {
                entry.setCssClass("progress-red");
            } else if ("Leyendo".equalsIgnoreCase(entry.getStatus())) {
                entry.setCssClass("progress-green");
            } else if ("Terminado".equalsIgnoreCase(entry.getStatus())) {
                entry.setCssClass("progress-blue");
            } else {
                entry.setCssClass("");
            }
        }
        model.addAttribute("leftJournalEntries", leftJournalEntries);
        model.addAttribute("rightJournalEntries", rightJournalEntries);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("hasNext", page < pageResult.getTotalPages() - 1);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        return "journal";
    }

}
