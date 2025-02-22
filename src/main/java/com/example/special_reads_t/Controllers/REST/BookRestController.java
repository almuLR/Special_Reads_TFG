package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Service.BookService;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookRestController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBook(@RequestParam String title) {

        List<Book> books = bookService.SearchBooksFromApi(title);
        return ResponseEntity.ok(books);
    }
}
