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
                .distinct().toList();

        return reviewRepo.findAllByRecommendTrueAndUserNot(user).stream()
                .filter(r -> r.getBook().getGenres().stream().anyMatch(favGenres::contains))
                .filter(r -> !journalRepo.existsByUserAndBook(user, r.getBook()))
                .limit(max)
                .toList();
    }

}

