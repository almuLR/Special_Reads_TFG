package com.example.special_reads_t.Repository;


import com.example.special_reads_t.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook_GoogleBookId(String googleBookId);
}
