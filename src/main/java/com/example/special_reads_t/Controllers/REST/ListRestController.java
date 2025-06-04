package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.WishList;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.UserService;
import com.example.special_reads_t.Service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/list")
public class ListRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private JournalService journalService;


    @GetMapping
    public ResponseEntity<List<WishList>> getWishList() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<WishList> entries = wishListService.getWishListFor(currentUser);
        return ResponseEntity.ok(entries);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addManualEntry(@RequestParam("title") String title) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        wishListService.addManualToWishList(currentUser, title);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/remove")
    public ResponseEntity<Void> removeFromWishList(@RequestParam("bookId") Long bookId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        wishListService.removeFromWishList(currentUser, bookId);
        return ResponseEntity.ok().build();
    }
}
