package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService;


    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(value = "title", required = false, defaultValue = "") String title) {
        List<Book> books = bookService.SearchBooksFromApi(title);
        return ResponseEntity.ok(books);
    }


    @GetMapping("/{googleBookId}")
    public ResponseEntity<BookDetailsResponse> getBookDetails(
            @PathVariable String googleBookId) {
        Book book = bookService.detailsBook(googleBookId);
        List<Review> reviews = reviewService.findByBook_GoogleBookId(googleBookId);

        // Cálculo de la calificación promedio decimal
        BigDecimal sumDecimal = reviews.stream()
                .map(Review::getDecimalRating)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long countDecimal = reviews.stream()
                .filter(r -> r.getDecimalRating() != null)
                .count();
        BigDecimal averageDecimal = BigDecimal.ZERO;
        if (countDecimal > 0) {
            averageDecimal = sumDecimal.divide(BigDecimal.valueOf(countDecimal), 2, RoundingMode.HALF_UP);
        }

        // Cálculo de la calificación promedio en estrellas
        double avgStars = reviews.stream()
                .mapToInt(Review::getStarRating)
                .average()
                .orElse(0.0);
        int roundedStars = (int) Math.round(avgStars);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<ReviewResponse> reviewResponses = reviews.stream().map(r -> {
            String formattedDate = r.getCreatedAt() != null
                    ? r.getCreatedAt().format(formatter)
                    : null;
            return new ReviewResponse(
                    r.getId(),
                    r.getReviewText(),
                    r.getDecimalRating(),
                    r.getStarRating(),
                    formattedDate
            );
        }).collect(Collectors.toList());

        BookDetailsResponse response = new BookDetailsResponse(
                book,
                averageDecimal,
                roundedStars,
                reviewResponses
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * DTO para respuesta de detalles de libro.
     */
    public static class BookDetailsResponse {
        private Book book;
        private BigDecimal averageRating;
        private int averageStars;
        private boolean isAverageStars5;
        private boolean isAverageStars4;
        private boolean isAverageStars3;
        private boolean isAverageStars2;
        private boolean isAverageStars1;
        private List<ReviewResponse> reviews;

        public BookDetailsResponse(Book book,
                                   BigDecimal averageRating,
                                   int averageStars,
                                   List<ReviewResponse> reviews) {
            this.book = book;
            this.averageRating = averageRating;
            this.averageStars = averageStars;
            this.isAverageStars5 = averageStars == 5;
            this.isAverageStars4 = averageStars == 4;
            this.isAverageStars3 = averageStars == 3;
            this.isAverageStars2 = averageStars == 2;
            this.isAverageStars1 = averageStars == 1;
            this.reviews = reviews;
        }

        public Book getBook() { return book; }
        public BigDecimal getAverageRating() { return averageRating; }
        public int getAverageStars() { return averageStars; }
        public boolean isAverageStars5() { return isAverageStars5; }
        public boolean isAverageStars4() { return isAverageStars4; }
        public boolean isAverageStars3() { return isAverageStars3; }
        public boolean isAverageStars2() { return isAverageStars2; }
        public boolean isAverageStars1() { return isAverageStars1; }
        public List<ReviewResponse> getReviews() { return reviews; }
    }

    /**
     * DTO para respuesta de reseñas.
     */
    public static class ReviewResponse {
        private Long id;
        private String comment;
        private BigDecimal decimalRating;
        private int starRating;
        private String formattedDate;

        public ReviewResponse(Long id,
                              String comment,
                              BigDecimal decimalRating,
                              int starRating,
                              String formattedDate) {
            this.id = id;
            this.comment = comment;
            this.decimalRating = decimalRating;
            this.starRating = starRating;
            this.formattedDate = formattedDate;
        }

        public Long getId() { return id; }
        public String getComment() { return comment; }
        public BigDecimal getDecimalRating() { return decimalRating; }
        public int getStarRating() { return starRating; }
        public String getFormattedDate() { return formattedDate; }

    }
}
