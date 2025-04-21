package com.example.special_reads_t.Integracion;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.BookRepository;
import com.example.special_reads_t.Repository.JournalRepository;
import com.example.special_reads_t.Repository.ReviewRepository;
import com.example.special_reads_t.Repository.UserRepository;
import com.example.special_reads_t.Service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
class RecommendationServiceIntegrationTest {

    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private JournalRepository journalRepository;

    private User alice;
    private User bob;
    private Book bookA;
    private Book bookB;
    private Book bookC;

    @BeforeEach
    void setUp() {
        // Users
        alice = new User();
        alice.setUsername("alice");
        userRepository.save(alice);

        bob = new User();
        bob.setUsername("bob");
        userRepository.save(bob);

        // Book A (Alice's favorite genres)
        bookA = new Book();
        bookA.setGenres(List.of("Fiction", "Adventure"));
        bookRepository.save(bookA);

        Review r1 = new Review();
        r1.setUser(alice);
        r1.setBook(bookA);
        r1.setRecommend(true);
        reviewRepository.save(r1);

        // Book B (bob's recommendation)
        bookB = new Book();
        bookB.setGenres(List.of("Adventure"));
        bookRepository.save(bookB);

        Review r2 = new Review();
        r2.setUser(bob);
        r2.setBook(bookB);
        r2.setRecommend(true);
        reviewRepository.save(r2);

        // Book C (bob's recommendation)
        bookC = new Book();
        bookC.setGenres(List.of("Sci-Fi"));
        bookRepository.save(bookC);

        Review r3 = new Review();
        r3.setUser(bob);
        r3.setBook(bookC);
        r3.setRecommend(true);
        reviewRepository.save(r3);

        reviewRepository.flush();
    }

    @Test
    void getRecommendationsFor_returnsOnlyMatchingGenresAndNotRead() {
        List<Review> recs = recommendationService.getRecommendationsFor(alice, 10);

        assertThat(recs)
                .hasSize(1)
                .extracting(r -> r.getBook())
                .containsExactly(bookB);
    }

    @Test
    void getRecommendationsFor_respectsMaxLimit() {
        Book extra = new Book();
        extra.setGenres(List.of("Adventure"));
        bookRepository.save(extra);

        Review r4 = new Review();
        r4.setUser(bob);
        r4.setBook(extra);
        r4.setRecommend(true);
        reviewRepository.save(r4);
        reviewRepository.flush();

        List<Review> recs = recommendationService.getRecommendationsFor(alice, 1);
        assertThat(recs).hasSize(1);
    }

    @Test
    void getRecommendationsFor_excludesAlreadyReadBooks() {
        // Alice reads bookB
        JournalEntry je = new JournalEntry();
        je.setUser(alice);
        je.setBook(bookB);
        je.setStatus("Terminado");
        journalRepository.save(je);
        journalRepository.flush();

        List<Review> recs = recommendationService.getRecommendationsFor(alice, 10);
        assertThat(recs).isEmpty();
    }
}
