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

        BigDecimal average = reviews.stream()
                .map(Review::getDecimalRating)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageScore = BigDecimal.ZERO;
        if (!reviews.isEmpty()) {
            long count = reviews.stream().filter(r -> r.getDecimalRating() != null).count();
            if (count > 0) {
                averageScore = average.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            }
        }
        model.addAttribute("averageRating", averageScore);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Review review : reviews) {
            if (review.getCreatedAt() != null) {
                String formattedDate = review.getCreatedAt().format(formatter);
                review.setFormattedDate(formattedDate);  // Necesitarás este campo
            }
        }

        double starAvg = reviews.stream()
                .mapToInt(Review::getStarRating)
                .average()
                .orElse(0.0);

        int rounded = (int) Math.round(starAvg);
        model.addAttribute("averageStarsIs5", rounded == 5);
        model.addAttribute("averageStarsIs4", rounded == 4);
        model.addAttribute("averageStarsIs3", rounded == 3);
        model.addAttribute("averageStarsIs2", rounded == 2);
        model.addAttribute("averageStarsIs1", rounded == 1);


        model.addAttribute("reviews", reviews);

        return "bookDetailsView";
    }


}
