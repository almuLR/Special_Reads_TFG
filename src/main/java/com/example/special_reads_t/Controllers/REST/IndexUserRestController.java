package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.WishList;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.RecommendationService;
import com.example.special_reads_t.Service.StatisticsService;
import com.example.special_reads_t.Service.UserService;
import com.example.special_reads_t.Service.WishListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/indexUser")
public class IndexUserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private StatisticsService statisticsService;

    /**
     * Obtiene datos del dashboard de usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<IndexUserResponse> getIndexData(HttpServletRequest request) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isAdmin = request.isUserInRole("ADMIN");

        // Recomendaciones y listados
        List<Review> recs = recommendationService.getRecommendationsFor(user, 8);
        List<JournalEntry> readingEntries = journalService.getReadingEntriesForUser(user);
        List<WishList> wishlistEntries = wishListService.getWishListFor(user);

        // Estadísticas
        long totRead = statisticsService.totalBooks(user);
        long totFriends = statisticsService.totalFriends(user);
        long totChallenges = statisticsService.completedChallenges(user);
        long totPages = statisticsService.totalPages(user);

        // Eventos de lectura y ordenación: usa getStatus() para diferenciar los estados
        List<JournalEntry> reading = journalService.getReadingEntriesForUser(user);
        List<JournalEntry> finished = journalService.getFinishedEntriesForUser(user);
        List<JournalEntry> combined = new ArrayList<>(reading);
        combined.addAll(finished);
        List<JournalEntry> lastEvents = combined.stream()
                .sorted(Comparator.comparing(
                        (JournalEntry entry) -> entry.getStatus() != null && entry.getStatus().equalsIgnoreCase("Leyendo")
                                ? entry.getStartDate() : entry.getFinishDate()
                ).reversed())
                .limit(7)
                .collect(Collectors.toList());

        IndexUserResponse response = new IndexUserResponse(
                user.getUsername(),
                isAdmin,
                recs,
                readingEntries,
                wishlistEntries,
                totRead,
                totFriends,
                totChallenges,
                totPages,
                lastEvents
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Añade un libro a la wishlist del usuario.
     */
    @PostMapping("/wishlist/add")
    public ResponseEntity<Void> addToWishlist(@RequestParam("bookId") Long bookId) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        wishListService.addToWishList(user, bookId);
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina un libro de la wishlist del usuario.
     */
    @PostMapping("/wishlist/remove")
    public ResponseEntity<Void> removeFromWishlist(@RequestParam("bookId") Long bookId) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        wishListService.removeFromWishList(user, bookId);
        return ResponseEntity.ok().build();
    }

    /**
     * DTO de respuesta para el endpoint /api/indexUser
     */
    public static class IndexUserResponse {
        private String username;
        private boolean admin;
        private List<Review> recs;
        private List<JournalEntry> readingEntries;
        private List<WishList> wishlistEntries;
        private long totRead;
        private long totFriends;
        private long totChallenges;
        private long totPages;
        private List<JournalEntry> lastEvents;

        public IndexUserResponse(String username,
                                 boolean admin,
                                 List<Review> recs,
                                 List<JournalEntry> readingEntries,
                                 List<WishList> wishlistEntries,
                                 long totRead,
                                 long totFriends,
                                 long totChallenges,
                                 long totPages,
                                 List<JournalEntry> lastEvents) {
            this.username = username;
            this.admin = admin;
            this.recs = recs;
            this.readingEntries = readingEntries;
            this.wishlistEntries = wishlistEntries;
            this.totRead = totRead;
            this.totFriends = totFriends;
            this.totChallenges = totChallenges;
            this.totPages = totPages;
            this.lastEvents = lastEvents;
        }

        // Getters y setters omitidos para brevedad
    }
}
