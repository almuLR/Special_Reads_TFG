package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    public Review save(Review review) {
        return reviewRepository.save(review);
    }
}
