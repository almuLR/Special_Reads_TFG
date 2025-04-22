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

    @PostMapping("/save")
    public ResponseEntity<Void> saveReview(@RequestBody ReviewRequestDto dto) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Book book = bookService.findById(dto.bookId);
        if (book == null) return ResponseEntity.notFound().build();

        Review review = new Review();
        review.setBook(book);
        review.setUser(currentUser);
        review.setStarRating(dto.starRating);
        review.setDecimalRating(dto.decimalRating);
        review.setFormat(dto.format);
        review.setPlotTwist(dto.plotTwist);
        review.setSpicy(dto.spicy);
        review.setFunny(dto.funny);
        review.setLove(dto.love);
        review.setPain(dto.pain);
        review.setAnger(dto.anger);
        review.setXd(dto.xd);
        review.setNeutral(dto.neutral);
        review.setFavoriteCharacter(dto.favoriteCharacter);
        review.setPointOfView(dto.pointOfView);
        review.setOneWord(dto.oneWord);
        review.setFavoriteQuote(dto.favoriteQuote);
        review.setRecommend(dto.recommend);
        review.setReviewText(dto.reviewText);
        review.setCreatedAt(LocalDateTime.now());

        reviewService.save(review);

        JournalEntry je = journalService.findByBookAndUser(book, currentUser);
        if (je != null) {
            je.setStatus("Terminado");
            je.setProgress(100);
            je.setRating(dto.starRating);
            je.setFinishDate(LocalDateTime.now());
            journalService.updateJournalEntry(je);
        }

        return ResponseEntity.ok().build();
    }

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

    public static class ReviewRequestDto {
        public Long bookId;
        public int starRating;
        public BigDecimal decimalRating;
        public String format;
        public int plotTwist;
        public int spicy;
        public int funny;
        public boolean love;
        public boolean pain;
        public boolean anger;
        public boolean xd;
        public boolean neutral;
        public String favoriteCharacter;
        public String pointOfView;
        public String oneWord;
        public String favoriteQuote;
        public boolean recommend;
        public String reviewText;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public int getStarRating() { return starRating; }
        public void setStarRating(int starRating) { this.starRating = starRating; }
        public BigDecimal getDecimalRating() { return decimalRating; }
        public void setDecimalRating(BigDecimal decimalRating) { this.decimalRating = decimalRating; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public int getPlotTwist() { return plotTwist; }
        public void setPlotTwist(int plotTwist) { this.plotTwist = plotTwist; }
        public int getSpicy() { return spicy; }
        public void setSpicy(int spicy) { this.spicy = spicy; }
        public int getFunny() { return funny; }
        public void setFunny(int funny) { this.funny = funny; }
        public boolean isLove() { return love; }
        public void setLove(boolean love) { this.love = love; }
        public boolean isPain() { return pain; }
        public void setPain(boolean pain) { this.pain = pain; }
        public boolean isAnger() { return anger; }
        public void setAnger(boolean anger) { this.anger = anger; }
        public boolean isXd() { return xd; }
        public void setXd(boolean xd) { this.xd = xd; }
        public boolean isNeutral() { return neutral; }
        public void setNeutral(boolean neutral) { this.neutral = neutral; }
        public String getFavoriteCharacter() { return favoriteCharacter; }
        public void setFavoriteCharacter(String favoriteCharacter) { this.favoriteCharacter = favoriteCharacter; }
        public String getPointOfView() { return pointOfView; }
        public void setPointOfView(String pointOfView) { this.pointOfView = pointOfView; }
        public String getOneWord() { return oneWord; }
        public void setOneWord(String oneWord) { this.oneWord = oneWord; }
        public String getFavoriteQuote() { return favoriteQuote; }
        public void setFavoriteQuote(String favoriteQuote) { this.favoriteQuote = favoriteQuote; }
        public boolean isRecommend() { return recommend; }
        public void setRecommend(boolean recommend) { this.recommend = recommend; }
        public String getReviewText() { return reviewText; }
        public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    }
}
