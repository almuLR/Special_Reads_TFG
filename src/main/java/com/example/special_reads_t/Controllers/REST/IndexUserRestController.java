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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @GetMapping
    public ResponseEntity<IndexUserResponse> getIndexData(HttpServletRequest request) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isAdmin = request.isUserInRole("ADMIN");

        List<Review> rawRecs = recommendationService.getRecommendationsFor(user, 8);
        List<ReviewDto> recs = rawRecs.stream()
                .map(r -> new ReviewDto(
                        r.getReviewText(),
                        r.isRecommend(),
                        r.getBook() != null ? r.getBook().getId() : null,
                        r.getBook() != null ? r.getBook().getTitle() : null
                )).toList();

        List<JournalEntry> reading = journalService.getReadingEntriesForUser(user);
        List<JournalEntry> finished = journalService.getFinishedEntriesForUser(user);
        List<JournalEntry> combined = new ArrayList<>(reading);
        combined.addAll(finished);

        List<JournalEntryDto> readingEntries = reading.stream().map(JournalEntryDto::from).toList();
        List<JournalEntryDto> lastEvents = combined.stream()
                .sorted(Comparator.comparing(
                        (JournalEntry entry) -> entry.getStatus() != null && entry.getStatus().equalsIgnoreCase("Leyendo")
                                ? entry.getStartDate() : entry.getFinishDate()
                ).reversed())
                .limit(7)
                .map(JournalEntryDto::from)
                .toList();

        List<WishListDto> wishlistEntries = wishListService.getWishListFor(user)
                .stream().map(WishListDto::from).toList();

        IndexUserResponse response = new IndexUserResponse(
                user.getUsername(),
                isAdmin,
                recs,
                readingEntries,
                wishlistEntries,
                statisticsService.totalBooks(user),
                statisticsService.totalFriends(user),
                statisticsService.completedChallenges(user),
                statisticsService.totalPages(user),
                lastEvents
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/wishlist/add")
    public ResponseEntity<Void> addToWishlist(@RequestParam("bookId") Long bookId) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        wishListService.addToWishList(user, bookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wishlist/remove")
    public ResponseEntity<Void> removeFromWishlist(@RequestParam("bookId") Long bookId) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        wishListService.removeFromWishList(user, bookId);
        return ResponseEntity.ok().build();
    }

    public static class IndexUserResponse {
        private String username;
        private boolean admin;
        private List<ReviewDto> recs;
        private List<JournalEntryDto> readingEntries;
        private List<WishListDto> wishlistEntries;
        private long totRead;
        private long totFriends;
        private long totChallenges;
        private long totPages;
        private List<JournalEntryDto> lastEvents;

        public IndexUserResponse(String username, boolean admin, List<ReviewDto> recs, List<JournalEntryDto> readingEntries,
                                 List<WishListDto> wishlistEntries, long totRead, long totFriends,
                                 long totChallenges, long totPages, List<JournalEntryDto> lastEvents) {
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

        public String getUsername() { return username; }
        public boolean isAdmin() { return admin; }
        public List<ReviewDto> getRecs() { return recs; }
        public List<JournalEntryDto> getReadingEntries() { return readingEntries; }
        public List<WishListDto> getWishlistEntries() { return wishlistEntries; }
        public long getTotRead() { return totRead; }
        public long getTotFriends() { return totFriends; }
        public long getTotChallenges() { return totChallenges; }
        public long getTotPages() { return totPages; }
        public List<JournalEntryDto> getLastEvents() { return lastEvents; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReviewDto {
        private String reviewText;
        private boolean recommend;
        private Long bookId;
        private String bookTitle;

        public ReviewDto(String reviewText, boolean recommend, Long bookId, String bookTitle) {
            this.reviewText = reviewText;
            this.recommend = recommend;
            this.bookId = bookId;
            this.bookTitle = bookTitle;
        }

        public String getReviewText() { return reviewText; }
        public boolean isRecommend() { return recommend; }
        public Long getBookId() { return bookId; }
        public String getBookTitle() { return bookTitle; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JournalEntryDto {
        private String status;
        private String startDate;
        private String finishDate;
        private Long bookId;
        private String bookTitle;

        public static JournalEntryDto from(JournalEntry entry) {
            JournalEntryDto dto = new JournalEntryDto();
            dto.status = entry.getStatus();
            dto.startDate = entry.getStartDate() != null ? entry.getStartDate().toString() : null;
            dto.finishDate = entry.getFinishDate() != null ? entry.getFinishDate().toString() : null;
            if (entry.getBook() != null) {
                dto.bookId = entry.getBook().getId();
                dto.bookTitle = entry.getBook().getTitle();
            }
            return dto;
        }

        public String getStatus() { return status; }
        public String getStartDate() { return startDate; }
        public String getFinishDate() { return finishDate; }
        public Long getBookId() { return bookId; }
        public String getBookTitle() { return bookTitle; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WishListDto {
        private Long bookId;
        private String bookTitle;

        public static WishListDto from(WishList entry) {
            WishListDto dto = new WishListDto();
            if (entry.getBook() != null) {
                dto.bookId = entry.getBook().getId();
                dto.bookTitle = entry.getBook().getTitle();
            }
            return dto;
        }

        public Long getBookId() { return bookId; }
        public String getBookTitle() { return bookTitle; }
    }
}
