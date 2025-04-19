package com.example.special_reads_t.Repository;


import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
    select distinct r.book 
      from Review r 
     where r.recommend = true 
       and r.book.id not in (
           select je.book.id 
             from JournalEntry je 
            where je.user = :user
        )
       and exists (
           select 1 
             from r.book.genres g 
            where g in :genres
        )
  """)
    List<Book> findRecommendedByGenresExcludingAlreadyRead(
            @Param("genres") Collection<String> genres,
            @Param("user") User user,
            Pageable pageable);

    List<Review> findAllByUserAndRecommendTrue(User user);
    List<Review> findAllByRecommendTrueAndUserNot(User user);


    List<Review> findByBook_GoogleBookId(String googleBookId);
}
