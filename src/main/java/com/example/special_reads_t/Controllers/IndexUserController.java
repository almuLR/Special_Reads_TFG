package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.ReadingEvent;
import com.example.special_reads_t.Service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexUserController {

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


    @GetMapping("/indexUser")
    public String indexUserTemplate(Model model, HttpServletRequest request) {
        User user = userService.getCurrentUser();

        List<JournalEntry> currentlyReadingEntries = journalService.getReadingEntriesForUser(user);
        List<Review> recs = recommendationService.getRecommendationsFor(user, 8);
        model.addAttribute("recs", recs);
        model.addAttribute("readingEntries", currentlyReadingEntries);

        model.addAttribute("wishlistEntries", wishListService.getWishListFor(user));

        model.addAttribute("userame", user.getUsername());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));

        model.addAttribute("totRead", statisticsService.totalBooks(user));
        model.addAttribute("totFriends", statisticsService.totalFriends(user) );
        model.addAttribute("totChallenges", statisticsService.completedChallenges(user));
        model.addAttribute("totPages", statisticsService.totalPages(user));

        // 1) Leo entries “leyendo”
        List<JournalEntry> reading = journalService.getReadingEntriesForUser(user);
        // 2) Leo entries “terminados”
        List<JournalEntry> finished = journalService.getFinishedEntriesForUser(user);

        // 3) Transformo cada entry en un “evento”
        List<JournalEntry> events = new ArrayList<>();
        events.addAll(reading);
        events.addAll(finished);

        // 4) Ordeno por fecha descendente (startDate o finishDate según status)
        events.sort((a,b) -> {
            LocalDateTime da = a.isReading() ? a.getStartDate() : a.getFinishDate();
            LocalDateTime db = b.isReading() ? b.getStartDate() : b.getFinishDate();
            return db.compareTo(da);
        });

        // 5) Limito a las últimas 7
        List<JournalEntry> last7 = events.stream()
                .limit(7)
                .toList();

        model.addAttribute("lastEvents", last7);

        return "indexUser";
    }

    @PostMapping("/indexUser/wishlist/add")
    @ResponseBody
    public ResponseEntity<?> saveToWishlist(@RequestParam("bookId") Long bookId) {
        User me = userService.getCurrentUser();
        wishListService.addToWishList(me, bookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/indexUser/wishlist/remove")
    @ResponseBody
    public ResponseEntity<?> removeFromWishlist(@RequestParam("bookId") Long bookId) {
        User me = userService.getCurrentUser();
        wishListService.removeFromWishList(me, bookId);
        return ResponseEntity.ok().build();
    }
}
