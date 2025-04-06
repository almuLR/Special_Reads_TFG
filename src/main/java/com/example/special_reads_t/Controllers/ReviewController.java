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

    @PostMapping("/review/save")
    public String saveReview(
            @RequestParam("bookId") Long bookId,
            @RequestParam("starRating") int starRating,
            @RequestParam(value = "decimalRating", required = true)BigDecimal decimalRating,
            @RequestParam("format") String format,
            @RequestParam("plotTwist") int plotTwist,
            @RequestParam("spicy") int spicy,
            @RequestParam("funny") int funny,
            @RequestParam(value = "love", required = false) String love,
            @RequestParam(value = "pain", required = false) String pain,
            @RequestParam(value = "anger", required = false) String anger,
            @RequestParam(value = "xd", required = false) String xd,
            @RequestParam(value = "neutral", required = false) String neutral,
            @RequestParam("favoriteCharacter") String favoriteCharacter,
            @RequestParam("pointOfView") String pointOfView,
            @RequestParam("oneWord") String oneWord,
            @RequestParam("favoriteQuote") String favoriteQuote,
            @RequestParam("recommend") boolean recommend,
            @RequestParam("reviewText") String reviewText
    ) {
        Book book = bookService.findById(bookId);
        if (book == null) {
            return "redirect:/journal";
        }

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }


        Review review = new Review();
        review.setBook(book);
        review.setUser(currentUser);
        review.setStarRating(starRating);
        review.setDecimalRating(decimalRating);
        review.setFormat(format);
        review.setPlotTwist(plotTwist);
        review.setSpicy(spicy);
        review.setFunny(funny);
        review.setLove(love != null);     // true si la checkbox "love" está marcada
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
        review.setUser(currentUser);
        review.setCreatedAt(LocalDateTime.now());

        reviewService.save(review);

        JournalEntry journalEntry = journalService.findByBookAndUser(book, currentUser);
        if(journalEntry != null) {
            journalEntry.setStatus("Terminado");
            journalEntry.setProgress(100);
            journalEntry.setRating(starRating);
            journalEntry.setFinishDate(LocalDateTime.now());
            journalService.updateJournalEntry(journalEntry);
        }


        return "redirect:/journal";
    }
}
