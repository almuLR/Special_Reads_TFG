package com.example.special_reads_t.Controllers;


import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String showjournal(Model model) {
        List<JournalEntry> entries = journalService.getAllJournalEntriesForUser();

        int maxTotal = 18;
        int maxLeft = 9;

        List<JournalEntry> leftJournalEntries = new ArrayList<>();
        List<JournalEntry> rightJournalEntries = new ArrayList<>();
        for (int i = 0; i < entries.size() && i < maxTotal; i++) {
            if (i < maxLeft) {
                leftJournalEntries.add(entries.get(i));
            } else {
                rightJournalEntries.add(entries.get(i));
            }
        }
        for (JournalEntry entry : entries) {
            if ("Pendiente".equalsIgnoreCase(entry.getStatus())) {
                entry.setCssClass("progress-red");
            } else if ("Leyendo".equalsIgnoreCase(entry.getStatus())) {
                entry.setCssClass("progress-green");
            } else if ("Terminado".equalsIgnoreCase(entry.getStatus())) {
                entry.setCssClass("progress-blue");
            }
        }
        model.addAttribute("leftJournalEntries", leftJournalEntries);
        model.addAttribute("rightJournalEntries", rightJournalEntries);
        return "journal";
    }

}
