package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.RecommendationService;
import com.example.special_reads_t.Service.UserService;
import com.example.special_reads_t.Service.WishListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
