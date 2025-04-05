package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    Optional<JournalEntry> findById(Long id);
    Optional<JournalEntry> findByBookAndUser(Book book, User user);
    Page<JournalEntry> findByUser(User user, Pageable pageable);
    Page<JournalEntry> findByUserAndStatus(User user, String status, Pageable pageable);
}
