package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
        List<Book> findAllByGoogleBookId(String googleBookId);

        Optional<Book> findByTitle(String title);
}
