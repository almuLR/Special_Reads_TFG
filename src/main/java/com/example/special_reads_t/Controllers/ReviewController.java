package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.BookRepository;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.ReviewService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ReviewController {

    @Autowired
    BookService bookService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    UserService userService;

    @Autowired
    JournalService journalService;


    @GetMapping("/review/{id}")
    public String review(@PathVariable("id") Long id, Model model) {
        JournalEntry journalEntry = journalService.findById(id);
        User currentUser = userService.getCurrentUser();
        if (journalEntry != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String formattedStartDate = journalEntry.getStartDate().format(formatter);
            model.addAttribute("startDateFormatted", formattedStartDate);

            if (journalEntry.getFinishDate() != null) {
                String formattedFinishDate = journalEntry.getFinishDate().format(formatter);
                model.addAttribute("finishDateFormatted", formattedFinishDate);
            }
        }
        Book book = journalEntry != null ? journalEntry.getBook() : null;
        model.addAttribute("entry", journalEntry);
        model.addAttribute("book", book);
        model.addAttribute("genre", "");

        // Extraer y pasar el primer género (si existe)
        if (book != null && book.getGenres() != null && !book.getGenres().isEmpty()) {
            model.addAttribute("genre", book.getGenres().get(0));
        }
        return "review";
    }

    @PostMapping("/review/save")
    public String saveReview(
            @RequestParam("bookId") Long bookId,
            @RequestParam(value = "starRating", required = false) Integer starRating,
            @RequestParam(value = "decimalRating", required = false) BigDecimal decimalRating,
            @RequestParam(value = "format", required = false) String format,
            @RequestParam(value = "plotTwist", required = false) Integer plotTwist,
            @RequestParam(value = "spicy", required = false) Integer spicy,
            @RequestParam(value = "funny", required = false) Integer funny,
            @RequestParam(value = "love", required = false) String love,
            @RequestParam(value = "pain", required = false) String pain,
            @RequestParam(value = "anger", required = false) String anger,
            @RequestParam(value = "xd", required = false) String xd,
            @RequestParam(value = "neutral", required = false) String neutral,
            @RequestParam(value = "favoriteCharacter", required = false) String favoriteCharacter,
            @RequestParam(value = "pointOfView", required = false) String pointOfView,
            @RequestParam(value = "oneWord", required = false) String oneWord,
            @RequestParam(value = "favoriteQuote", required = false) String favoriteQuote,
            @RequestParam(value = "recommend", required = false) Boolean recommend,
            @RequestParam(value = "reviewText", required = false) String reviewText,
            Model model
    ) {
        Book book = bookService.findById(bookId);
        if (book == null) return "redirect:/journal";

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return "redirect:/login";

        if (starRating == null || decimalRating == null || format == null || format.isBlank()
                || plotTwist == null || funny == null
                || favoriteCharacter == null || favoriteCharacter.isBlank()
                || pointOfView == null || pointOfView.isBlank()
                || oneWord == null || oneWord.isBlank()
                || favoriteQuote == null || favoriteQuote.isBlank()
                || recommend == null || reviewText == null || reviewText.isBlank()) {

            model.addAttribute("error", "Por favor, completa todos los campos obligatorios.");
            model.addAttribute("book", book);
            model.addAttribute("genre", book.getGenres().isEmpty() ? "" : book.getGenres().get(0));
            model.addAttribute("entry", journalService.findByBookAndUser(book, currentUser));
            return "review";
        }

        if (reviewService.existsByUserAndBook(currentUser, book)) {
            model.addAttribute("error", "Ya has enviado una reseña para este libro.");
            return "review";
        }
        if (decimalRating.compareTo(new BigDecimal(10)) > 0) {
            decimalRating = new BigDecimal(10);
        } else if (decimalRating.compareTo(BigDecimal.ZERO) < 0) {
            decimalRating = BigDecimal.ZERO;
        }

        Review review = new Review();
        review.setBook(book);
        review.setUser(currentUser);
        review.setStarRating(starRating);
        review.setDecimalRating(decimalRating);
        review.setFormat(format);
        review.setPlotTwist(plotTwist);
        review.setSpicy(spicy != null ? spicy : 0);
        review.setFunny(funny);
        review.setLove(love != null);
        review.setPain(pain != null);
        review.setAnger(anger != null);
        review.setXd(xd != null);
        review.setNeutral(neutral != null);
        review.setFavoriteCharacter(favoriteCharacter);
        review.setPointOfView(pointOfView);
        review.setOneWord(oneWord);
        review.setFavoriteQuote(favoriteQuote);
        review.setRecommend(recommend);
        review.setReviewText(reviewText);
        review.setCreatedAt(LocalDateTime.now());

        reviewService.save(review);

        JournalEntry journalEntry = journalService.findByBookAndUser(book, currentUser);
        if (journalEntry != null) {
            journalEntry.setStatus("Terminado");
            journalEntry.setProgress(100);
            journalEntry.setRating(starRating);
            journalEntry.setFinishDate(LocalDateTime.now());
            journalService.updateJournalEntry(journalEntry);
        } else {
            model.addAttribute("error", "Ya has enviado una reseña para este libro.");
        }

        return "redirect:/journal";
    }

}
