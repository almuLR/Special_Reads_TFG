package com.example.special_reads_t.Service;


import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    public JournalEntry addBookToJournal(Book book) {
        if (book.getId() == null) {
            book = bookService.save(book);
        }
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No se encontró el usuario actual");
        }

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setBook(book);
        journalEntry.setUser(currentUser);
        journalEntry.setProgress(0);
        journalEntry.setStatus("Leyendo");
        journalEntry.setStartDate(LocalDateTime.now());
        return journalRepository.save(journalEntry);
    }

    public JournalEntry findById(Long id) {
        Optional<JournalEntry> journalEntry = journalRepository.findById(id);
        return journalEntry.orElse(null);
    }

    public JournalEntry updateJournalEntry(JournalEntry journalEntry) {
        return journalRepository.save(journalEntry);
    }

    public List<JournalEntry> getAllJournalEntriesForUser() {
        return journalRepository.findAll();
    }

    public JournalEntry save(JournalEntry entry) {
        return journalRepository.save(entry);
    }

    public JournalEntry findByBookAndUser(Book book, User user) {
        Optional<JournalEntry> journalEntry = journalRepository.findByBookAndUser(book, user);
        return journalEntry.orElse(null);
    }

    public Page<JournalEntry> getAllEntriesForUser(User user, int page) {
        // PageRequest.of(<númeroPágina>, <tamañoPágina>)
        // En tu caso, si sigues usando 18 para 9+9, lo pones como tamaño de página.
        return journalRepository.findByUser(user, PageRequest.of(page, 18));
    }


    public Page<JournalEntry> getFinishedEntriesForUser(User user, int page) {
        return journalRepository.findByUserAndStatus(user, "Terminado", PageRequest.of(page, 21));
    }

    public List<JournalEntry> getReadingEntriesForUser(User user) {
        return journalRepository.findByUserAndStatusIgnoreCase(user, "Leyendo");
    }
}
