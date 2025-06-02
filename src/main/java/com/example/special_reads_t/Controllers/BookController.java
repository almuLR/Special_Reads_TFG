package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    ReviewService reviewService;

    @GetMapping("/book/search")
    public String serchBooks(@RequestParam(value = "title", required = false, defaultValue = "") String title, Model model) {
        List<Book> books = bookService.SearchBooksFromApi(title);
        model.addAttribute("books", books);
        return "bookSearchs";
    }

    @GetMapping("/detailsBook/{googleBookId}")
    public String detailsBook(@PathVariable("googleBookId") String googleBookId, Model model) {
        Book bookDetails = bookService.detailsBook(googleBookId);
        model.addAttribute("book", bookDetails);

        List<Review> reviews = reviewService.findByBook_GoogleBookId(googleBookId);
        model.addAttribute("reviews", reviews);

        // Calcular media decimal
        BigDecimal averageScore = BigDecimal.ZERO;
        long validDecimalCount = reviews.stream().filter(r -> r.getDecimalRating() != null).count();
        if (validDecimalCount > 0) {
            BigDecimal sum = reviews.stream()
                    .map(Review::getDecimalRating)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averageScore = sum.divide(BigDecimal.valueOf(validDecimalCount), 2, RoundingMode.HALF_UP);
        }
        model.addAttribute("averageRating", averageScore);

        // Calcular estrellas promedio (de 0 a 5)
        double starAvg = reviews.stream()
                .mapToInt(r -> 6 - r.getStarRating())
                .average()
                .orElse(0.0);

        int starCount = (int) Math.floor(starAvg);
        List<Boolean> avgFilledStars = new ArrayList<>();
        List<Boolean> avgEmptyStars = new ArrayList<>();
        for (int i = 0; i < starCount; i++) avgFilledStars.add(true);
        for (int i = starCount; i < 5; i++) avgEmptyStars.add(true);
        model.addAttribute("avgFilledStars", avgFilledStars);
        model.addAttribute("avgEmptyStars", avgEmptyStars);

        // Formatear fecha y preparar estrellas individuales por reseña
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Review review : reviews) {
            if (review.getCreatedAt() != null) {
                review.setFormattedDate(review.getCreatedAt().format(formatter));
            }

            int stars = 6 - review.getStarRating();
            List<Boolean> filled = new ArrayList<>();
            List<Boolean> empty = new ArrayList<>();
            for (int i = 0; i < stars; i++) filled.add(true);
            for (int i = stars; i < 5; i++) empty.add(true);
            review.setFilledStars(filled);
            review.setEmptyStars(empty);
        }

        return "bookDetailsView";
    }


}
