package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
