package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    Optional<JournalEntry> findById(Long id);
    List<JournalEntry> findAllByUser_Username(String username);
}
