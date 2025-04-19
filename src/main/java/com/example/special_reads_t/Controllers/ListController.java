package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.UserService;
import com.example.special_reads_t.Service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {

    @Autowired
    private UserService userService;
    @Autowired
    private WishListService wishListService;
    @Autowired
    private JournalService journalService;

    @GetMapping("/list")
    public String listTemplate(Model model) {
        User u = userService.getCurrentUser();
        model.addAttribute("wishlistEntries", wishListService.getWishListFor(u));
        return "list";
    }

    @PostMapping("/list/add")
    @ResponseBody
    public ResponseEntity<?> addManual(
            @RequestParam("title") String title) {
        User u = userService.getCurrentUser();
        wishListService.addManualToWishList(u, title);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/list/wishlist/remove")
    @ResponseBody
    public ResponseEntity<?> removeFromWishlist(
            @RequestParam("bookId") Long id
    ) {
        User u = userService.getCurrentUser();
        wishListService.removeFromWishList(u, id);
        return ResponseEntity.ok().build();
    }
}
