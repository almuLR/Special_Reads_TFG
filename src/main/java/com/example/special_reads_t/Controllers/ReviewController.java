package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Repository.BookRepository;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ReviewController {

    @Autowired
    BookService bookService;

    @Autowired
    JournalService journalService;
    @GetMapping("/review/{id}")
    public String review(@PathVariable("id") Long id, Model model) {
        JournalEntry journalEntry = journalService.findById(id);
        if (journalEntry != null && journalEntry.getFinishDate() == null) {
            journalEntry.setFinishDate(LocalDateTime.now());
            journalService.updateJournalEntry(journalEntry);
        }
        // Formateamos la fecha para que muestre solo la parte de la fecha (sin hora)
        if (journalEntry != null && journalEntry.getFinishDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedFinishDate = journalEntry.getFinishDate().format(formatter);
            String formattedStartDate = journalEntry.getStartDate().format(formatter);

            model.addAttribute("finishDateFormatted", formattedFinishDate);
            model.addAttribute("startDateFormatted", formattedStartDate);
        }
        Book book = journalEntry != null ? journalEntry.getBook() : null;
        model.addAttribute("entry", journalEntry);
        model.addAttribute("book", book);

        // Extraer y pasar el primer género (si existe)
        if (book != null && book.getGenres() != null && !book.getGenres().isEmpty()) {
            model.addAttribute("genre", book.getGenres().get(0));
        }
        return "review";
    }
}
