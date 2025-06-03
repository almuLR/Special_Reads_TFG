package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> findByBook_GoogleBookId(String googleBookId) {
        return reviewRepository.findByBook_GoogleBookId(googleBookId);
    }

    public Review findByUserAndBook(User user, Book book) {
        return reviewRepository.findByUserAndBook(user, book);
    };

    public boolean existsByUserAndBook(User user, Book book){
        return reviewRepository.existsByUserAndBook(user, book);
    }
}
