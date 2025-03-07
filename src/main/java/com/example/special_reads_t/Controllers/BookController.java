package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookController {

    @Autowired
    BookService bookService;

    @GetMapping("/book/search")
    public String serchBooks(@RequestParam(value = "title", required = false, defaultValue = "") String title, Model model) {
        List<Book> books = bookService.SearchBooksFromApi(title);
        model.addAttribute("books", books);
        return "bookSearchs";
    }

    @GetMapping("/detailsBook/{googleBookId}")
    public String detailsBook(@PathVariable("googleBookId") String googleBookId, Model model) {
        Book bookDetails = bookService.detailsBook(googleBookId);
        model.addAttribute("book", bookDetails);
        return "bookDetailsView";
    }

    @GetMapping("/apiResponse")
    public ResponseEntity<String> getApiResponse(@RequestParam String title) {
        bookService.testApiResponse(title);
        return ResponseEntity.ok("Revisa la consola para ver la respuesta de la API");
    }
}
