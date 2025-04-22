package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookshelf")
public class BookShelfRestController {

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;

    /**
     * Devuelve la distribución de libros terminados del usuario en 3 filas de 7 celdas.
     * @param page Página de la paginación (0-index).
     * @return Estructura con filas y datos de paginación.
     */
    @GetMapping
    public ResponseEntity<BookshelfResponse> getBookshelf(
            @RequestParam(defaultValue = "0") int page) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Page<JournalEntry> pageResult = journalService.getFinishedEntriesForUser(currentUser, page);
        List<JournalEntry> finishedBooks = pageResult.getContent();

        int totalSlots = 21;
        int slotsPerRow = 7;
        List<JournalEntry> row1 = new ArrayList<>(slotsPerRow);
        List<JournalEntry> row2 = new ArrayList<>(slotsPerRow);
        List<JournalEntry> row3 = new ArrayList<>(slotsPerRow);

        for (int i = 0; i < totalSlots; i++) {
            JournalEntry entry = i < finishedBooks.size() ? finishedBooks.get(i) : null;
            if (i < slotsPerRow) {
                row1.add(entry);
            } else if (i < slotsPerRow * 2) {
                row2.add(entry);
            } else {
                row3.add(entry);
            }
        }

        BookshelfResponse response = new BookshelfResponse(
                row1,
                row2,
                row3,
                page,
                pageResult.getTotalPages(),
                page > 0,
                page < pageResult.getTotalPages() - 1,
                page - 1,
                page + 1
        );
        return ResponseEntity.ok(response);
    }

    /**
     * DTO para la respuesta del bookshelf REST.
     */
    public static class BookshelfResponse {
        private List<JournalEntry> row1;
        private List<JournalEntry> row2;
        private List<JournalEntry> row3;
        private int currentPage;
        private int totalPages;
        private boolean hasPrevious;
        private boolean hasNext;
        private int prevPage;
        private int nextPage;

        public BookshelfResponse(List<JournalEntry> row1,
                                 List<JournalEntry> row2,
                                 List<JournalEntry> row3,
                                 int currentPage,
                                 int totalPages,
                                 boolean hasPrevious,
                                 boolean hasNext,
                                 int prevPage,
                                 int nextPage) {
            this.row1 = row1;
            this.row2 = row2;
            this.row3 = row3;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.hasPrevious = hasPrevious;
            this.hasNext = hasNext;
            this.prevPage = prevPage;
            this.nextPage = nextPage;
        }
        public List<JournalEntry> getRow1() { return row1; }
        public List<JournalEntry> getRow2() { return row2; }
        public List<JournalEntry> getRow3() { return row3; }
        public int getCurrentPage() { return currentPage; }
        public int getTotalPages() { return totalPages; }
        public boolean isHasPrevious() { return hasPrevious; }
        public boolean isHasNext() { return hasNext; }
        public int getPrevPage() { return prevPage; }
        public int getNextPage() { return nextPage; }

    }
}
