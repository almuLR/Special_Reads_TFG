package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/journal")
public class JournalRestController {

    @Autowired
    private BookService bookService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;


    @PostMapping("/add/{googleBookId}")
    public ResponseEntity<JournalEntry> addBookToJournal(@PathVariable String googleBookId) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Book book = bookService.detailsBook(googleBookId);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        JournalEntry entry = journalService.addBookToJournal(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }


    @PostMapping("/updateProgress")
    public ResponseEntity<JournalEntry> updateProgress(
            @RequestParam Long journalEntryId,
            @RequestParam String progressType,
            @RequestParam int progressValue,
            @RequestParam String status) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        JournalEntry entry = journalService.findById(journalEntryId);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        if ("pages".equalsIgnoreCase(progressType)) {
            int totalPages = entry.getBook().getPageCount();
            if (totalPages > 0) {
                int percentage = (int) ((progressValue / (double) totalPages) * 100);
                entry.setProgress(percentage);
            }
        } else if ("percentage".equalsIgnoreCase(progressType)) {
            entry.setProgress(progressValue);
        }
        entry.setStatus(status);
        JournalEntry updated = journalService.updateJournalEntry(entry);
        return ResponseEntity.ok(updated);
    }


    @GetMapping
    public ResponseEntity<JournalPageResponse> getJournal(
            @RequestParam(defaultValue = "0") int page) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<JournalEntry> pageResult = journalService.getAllEntriesForUser(currentUser, page);
        List<JournalEntry> entries = pageResult.getContent();

        int slotsPerSide = 9;
        List<JournalEntry> left = entries.stream().limit(slotsPerSide).collect(Collectors.toList());
        List<JournalEntry> right = entries.stream().skip(slotsPerSide).limit(slotsPerSide).collect(Collectors.toList());

        entries.forEach(entry -> {
            switch (entry.getStatus().toLowerCase()) {
                case "pendiente": entry.setCssClass("progress-red"); break;
                case "leyendo": entry.setCssClass("progress-green"); break;
                case "terminado": entry.setCssClass("progress-blue"); break;
                default: entry.setCssClass("");
            }
        });

        JournalPageResponse response = new JournalPageResponse(
                left, right,
                pageResult.getNumber(), pageResult.getTotalPages(),
                pageResult.hasPrevious(), pageResult.hasNext(),
                pageResult.getNumber() - 1, pageResult.getNumber() + 1
        );
        return ResponseEntity.ok(response);
    }


    public static class JournalPageResponse {
        private List<JournalEntry> leftJournalEntries;
        private List<JournalEntry> rightJournalEntries;
        private int currentPage;
        private int totalPages;
        private boolean hasPrevious;
        private boolean hasNext;
        private int prevPage;
        private int nextPage;

        public JournalPageResponse(List<JournalEntry> leftJournalEntries,
                                   List<JournalEntry> rightJournalEntries,
                                   int currentPage,
                                   int totalPages,
                                   boolean hasPrevious,
                                   boolean hasNext,
                                   int prevPage,
                                   int nextPage) {
            this.leftJournalEntries = leftJournalEntries;
            this.rightJournalEntries = rightJournalEntries;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.hasPrevious = hasPrevious;
            this.hasNext = hasNext;
            this.prevPage = prevPage;
            this.nextPage = nextPage;
        }
        public List<JournalEntry> getLeftJournalEntries() {
            return leftJournalEntries;
        }

        public void setLeftJournalEntries(List<JournalEntry> leftJournalEntries) {
            this.leftJournalEntries = leftJournalEntries;
        }

        public List<JournalEntry> getRightJournalEntries() {
            return rightJournalEntries;
        }

        public void setRightJournalEntries(List<JournalEntry> rightJournalEntries) {
            this.rightJournalEntries = rightJournalEntries;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public int getPrevPage() {
            return prevPage;
        }

        public void setPrevPage(int prevPage) {
            this.prevPage = prevPage;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

    }
}
