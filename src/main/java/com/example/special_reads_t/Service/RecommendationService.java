package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.JournalRepository;
import com.example.special_reads_t.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired private JournalRepository journalRepo;
    @Autowired private UserService userService;

    public List<Review> getRecommendationsFor(User user, int max) {
        List<String> favGenres = reviewRepo.findAllByUserAndRecommendTrue(user).stream()
                .flatMap(r -> r.getBook().getGenres().stream())
                .map(g -> g.toLowerCase().trim())
                .distinct()
                .toList();

        List<Review> candidates = reviewRepo.findAllByRecommendTrueAndUserNot(user);

        System.out.println("Total posibles recomendaciones: " + candidates.size());
        System.out.println("Géneros favoritos: " + favGenres);

        List<Review> recommendations = candidates.stream()
                .peek(r -> {
                    List<String> bookGenres = r.getBook().getGenres().stream()
                            .map(g -> g.toLowerCase().trim())
                            .toList();
                    boolean sameGenre = bookGenres.stream().anyMatch(favGenres::contains);
                    boolean notRead = !journalRepo.existsByUserAndBook(user, r.getBook());
                    if (sameGenre && notRead) {
                        System.out.println("Recomendado: " + r.getBook().getTitle());
                    } else {
                        System.out.println("Descartado: " + r.getBook().getTitle() +
                                " | Género coincide: " + sameGenre +
                                " | Ya leído: " + !notRead);
                    }
                })
                .filter(r -> r.getBook().getGenres().stream()
                        .map(g -> g.toLowerCase().trim())
                        .anyMatch(favGenres::contains))
                .filter(r -> !journalRepo.existsByUserAndBook(user, r.getBook()))
                .limit(max)
                .toList();

        if (recommendations.isEmpty()) {
            recommendations = candidates.stream()
                    .filter(r -> !journalRepo.existsByUserAndBook(user, r.getBook()))
                    .sorted((a, b) -> Double.compare(
                            b.getDecimalRating().doubleValue(),
                            a.getDecimalRating().doubleValue()))
                    .limit(max)
                    .toList();
            System.out.println("↪Usando fallback por puntuación");
        }

        return recommendations;
    }


}

