package com.example.special_reads_t.Service;


import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.WishList;
import com.example.special_reads_t.Model.dto.ReadingEvent;
import com.example.special_reads_t.Repository.BookRepository;
import com.example.special_reads_t.Repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

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
        return journalRepository.findByUser(user, PageRequest.of(page, 18));
    }


    public Page<JournalEntry> getFinishedEntriesForUser(User user, int page) {
        return journalRepository.findByUserAndStatus(user, "Terminado", PageRequest.of(page, 21));
    }

    public List<JournalEntry> getFinishedEntriesForUser(User user) {
        return journalRepository.findByUserAndStatus(user, "Terminado");
    }

    public List<JournalEntry> getReadingEntriesForUser(User user) {
        return journalRepository.findByUserAndStatusIgnoreCase(user, "Leyendo");
    }

    public Long countFinishedEntriesForUser(User user) {
        return journalRepository.countByUserAndStatus(user, "Terminado");
    }

    public List<ReadingEvent> getLatestEvents(User user, int max) {
        // 1) traes todas las entradas del usuario
        List<JournalEntry> entries = journalRepository.findByUser(user);
        List<ReadingEvent> events = new ArrayList<>();

        for (JournalEntry je : entries) {
            // si tiene startDate → “Leyendo”
            if (je.getStartDate() != null) {
                events.add(new ReadingEvent(
                        je.getBook().getTitle(),
                        je.getStartDate(),
                        "Leyendo"
                ));
            }
            // si tiene finishDate → “Leído”
            if (je.getFinishDate() != null) {
                events.add(new ReadingEvent(
                        je.getBook().getTitle(),
                        je.getFinishDate(),
                        "Leído"
                ));
            }
        }

        return events.stream()
                .sorted(Comparator.comparing(ReadingEvent::getDate).reversed())
                .limit(max)
                .toList();
    }

}

