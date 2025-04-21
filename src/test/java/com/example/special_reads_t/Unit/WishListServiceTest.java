package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.Friend;
import com.example.special_reads_t.Model.WishList;
import com.example.special_reads_t.Model.dto.FriendJournalProgressDto;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.BookRepository;
import com.example.special_reads_t.Repository.FriendRepository;
import com.example.special_reads_t.Repository.WishListRepository;
import com.example.special_reads_t.Security.jwt.JwtTokenProvider;
import com.example.special_reads_t.Service.FriendService;
import com.example.special_reads_t.Service.UserService;
import com.example.special_reads_t.Service.WishListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WishListServiceTest {

    @Mock
    private WishListRepository wishListRepo;

    @Mock
    private BookRepository bookRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private WishListService wishListService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        book = new Book();
        book.setId(10L);
        book.setTitle("Test Book");
    }

    @DisplayName("getWishListFor returns list from repository")
    @Test
    void getWishListFor_ReturnsList() {
        WishList entry = new WishList(user, book);
        when(wishListRepo.findByUser(user)).thenReturn(List.of(entry));

        List<WishList> result = wishListService.getWishListFor(user);

        assertThat(result).hasSize(1).contains(entry);
        verify(wishListRepo).findByUser(user);
    }

    @DisplayName("addToWishList saves when not exists")
    @Test
    void addToWishList_SavesNewEntry() {
        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
        when(wishListRepo.findByUser(user)).thenReturn(List.of());

        wishListService.addToWishList(user, 10L);

        verify(wishListRepo).save(any(WishList.class));
    }

    @DisplayName("addToWishList avoids duplicates")
    @Test
    void addToWishList_NoDuplicate() {
        WishList existing = new WishList(user, book);
        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
        when(wishListRepo.findByUser(user)).thenReturn(List.of(existing));

        wishListService.addToWishList(user, 10L);

        verify(wishListRepo, never()).save(any());
    }

    @DisplayName("addManualToWishList creates new book if not exist")
    @Test
    void addManualToWishList_CreatesBook() {
        String title = "Manual Book";
        when(bookRepo.findByTitle(title)).thenReturn(Optional.empty());
        when(bookRepo.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(20L);
            return b;
        });

        wishListService.addManualToWishList(user, title);

        verify(bookRepo).save(any(Book.class));
        verify(wishListRepo).save(any(WishList.class));
    }

    @DisplayName("removeFromWishList calls repository delete method")
    @Test
    void removeFromWishList_DeletesEntry() {
        wishListService.removeFromWishList(user, 10L);

        verify(wishListRepo).deleteByUserAndBookId(user, 10L);
    }
}

