package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.WishList;
import com.example.special_reads_t.Repository.BookRepository;
import com.example.special_reads_t.Repository.WishListRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@Service
public class WishListService {
    @Autowired
    private WishListRepository wishListRepo;
    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserService userService;

    public List<WishList> getWishListFor(User user) {
        return wishListRepo.findByUser(user);
    }

    @Transactional
    public void addToWishList(User user, Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        // evita duplicados:
        boolean exists = wishListRepo.findByUser(user)
                .stream()
                .anyMatch(w -> w.getBook().getId().equals(bookId));
        if (!exists) {
            wishListRepo.save(new WishList(user, book));
        }
    }
    @Transactional
    public void addManualToWishList(User user, String title) {
        Book book = bookRepo.findByTitle(title).orElse(null);

        if (book == null) {
            book = new Book();
            book.setTitle(title);
            book.setAuthor("Desconocido");
            book.setIsbn("Manual-" + System.currentTimeMillis());
            book = bookRepo.save(book);
        }

        WishList entry = new WishList();
        entry.setUser(user);
        entry.setManualTitle(title);
        entry.setBook(book);

        wishListRepo.save(entry);

    }
    @Transactional
    public void removeFromWishList(User user, Long bookId) {
        wishListRepo.deleteByUserAndBookId(user, bookId);
    }
}