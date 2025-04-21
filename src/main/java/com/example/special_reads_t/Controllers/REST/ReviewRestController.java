package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.ReviewService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controlador REST para la gestión de reseñas.
 */
@RestController
@RequestMapping("/api/review")
public class ReviewRestController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private JournalService journalService;

    /**
     * GET /api/review/{id}
     * Marca fecha de finalización y devuelve datos para revisar un journal entry.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        JournalEntry journalEntry = journalService.findById(id);
        if (journalEntry == null) {
            return ResponseEntity.notFound().build();
        }
        if (journalEntry.getFinishDate() == null) {
            journalEntry.setFinishDate(LocalDateTime.now());
            journalService.updateJournalEntry(journalEntry);
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String finishDate = journalEntry.getFinishDate().format(fmt);
        String startDate = journalEntry.getStartDate().format(fmt);
        Book book = journalEntry.getBook();
        String genre = (book != null && book.getGenres() != null && !book.getGenres().isEmpty())
                ? book.getGenres().get(0) : null;
        ReviewResponse resp = new ReviewResponse(journalEntry, book, startDate, finishDate, genre);
        return ResponseEntity.ok(resp);
    }

    /**
     * POST /api/review/save
     * Guarda una reseña y actualiza el journal entry.
     */
    @PostMapping("/save")
    public ResponseEntity<Void> saveReview(
            @RequestParam Long bookId,
            @RequestParam int starRating,
            @RequestParam BigDecimal decimalRating,
            @RequestParam String format,
            @RequestParam int plotTwist,
            @RequestParam int spicy,
            @RequestParam int funny,
            @RequestParam(required = false) String love,
            @RequestParam(required = false) String pain,
            @RequestParam(required = false) String anger,
            @RequestParam(required = false) String xd,
            @RequestParam(required = false) String neutral,
            @RequestParam String favoriteCharacter,
            @RequestParam String pointOfView,
            @RequestParam String oneWord,
            @RequestParam String favoriteQuote,
            @RequestParam boolean recommend,
            @RequestParam String reviewText
    ) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Book book = bookService.findById(bookId);
        if (book == null) {
            return ResponseEntity.notFound().build();
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
        JournalEntry je = journalService.findByBookAndUser(book, currentUser);
        if (je != null) {
            je.setStatus("Terminado");
            je.setProgress(100);
            je.setRating(starRating);
            je.setFinishDate(LocalDateTime.now());
            journalService.updateJournalEntry(je);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * DTO de respuesta para GET /api/review/{id}
     */
    public static class ReviewResponse {
        public Long entryId;
        public Long bookId;
        public String startDate;
        public String finishDate;
        public String genre;
        public String title;

        public ReviewResponse(JournalEntry je, Book book, String startDate, String finishDate, String genre) {
            this.entryId = je.getId();
            this.bookId = book.getId();
            this.title = book.getTitle();
            this.startDate = startDate;
            this.finishDate = finishDate;
            this.genre = genre;
        }
    }
}
