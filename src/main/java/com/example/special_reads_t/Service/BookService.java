package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.dto.GoogleBookResult;
import com.example.special_reads_t.Model.dto.GoogleBooksResponse;
import com.example.special_reads_t.Model.dto.VolumeInfo;
import com.example.special_reads_t.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Book> SearchBooksFromApi(String title) {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&maxResults=40&startIndex=0";

        ResponseEntity<GoogleBooksResponse> response = restTemplate.getForEntity(apiUrl, GoogleBooksResponse.class);
        GoogleBooksResponse googleBooksResponse = response.getBody();


        if (googleBooksResponse != null && googleBooksResponse.getResults() != null) {
            return googleBooksResponse.getResults().stream().map(result -> {
                VolumeInfo info = result.getVolumeInfo();
                Book book = new Book();
                book.setGoogleBookId(result.getGoogleBookId());
                book.setTitle(info.getTitle());
                if (info.getAuthors() != null && !info.getAuthors().isEmpty()) {
                    book.setAuthor(info.getAuthors().get(0));
                } else {
                    book.setAuthor("Unknown");
                }
                book.setPublisher(info.getPublisher());
                book.setPublishedDate(info.getPublishedDate());
                book.setSynopsis(info.getDescription());
                book.setPageCount(info.getPageCount() != null ? info.getPageCount() : 0);
                if (info.getCategories() != null && !info.getCategories().isEmpty()) {
                    book.setGenres(info.getCategories());
                }else {
                    book.setGenres(Collections.singletonList("Unknown"));
                }
                if (info.getImageLinks() != null) {
                    book.setCoverImageUrl(info.getImageLinks().getThumbnail());
                } else {
                    book.setCoverImageUrl("/images/bookDefault.jpg");
                }

                return book;
            }).collect(Collectors.toList());


        }

        return Collections.emptyList();
    }

    public Book detailsBook(String googleBookId) {
        // Construye la URL usando el ID del libro
        String apiUrl = "https://www.googleapis.com/books/v1/volumes/" + googleBookId;
        // Realiza la petición y mapea la respuesta a un objeto GoogleBookItem
        ResponseEntity<GoogleBookResult> response = restTemplate.getForEntity(apiUrl, GoogleBookResult.class);
        GoogleBookResult result = response.getBody();

        if (result != null && result.getVolumeInfo() != null) {
            VolumeInfo info = result.getVolumeInfo();
            Book book = new Book();
            book.setTitle(info.getTitle());
            // Asigna el ID de la API al campo googleBookId
            book.setGoogleBookId(result.getGoogleBookId());
            if (info.getAuthors() != null && !info.getAuthors().isEmpty()) {
                book.setAuthor(info.getAuthors().get(0));
            }
            book.setPublisher(info.getPublisher());
            book.setPublishedDate(info.getPublishedDate());
            book.setSynopsis(info.getDescription());
            System.out.println("Páginas recibidas de la API: " + info.getPageCount());
            book.setPageCount(info.getPageCount() != null ? info.getPageCount() : 0);
            if (info.getCategories() != null && !info.getCategories().isEmpty()) {
                book.setGenres(info.getCategories());
            } else {
                book.setGenres(Collections.singletonList("Unknown"));
            }
            if (info.getImageLinks() != null) {
                book.setCoverImageUrl(info.getImageLinks().getThumbnail());
            }
            return book;
        }

        return null;
    }
    public void testApiResponse(String title) {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title;
        String jsonResponse = restTemplate.getForObject(apiUrl, String.class);
        System.out.println("Respuesta cruda de la API: " + jsonResponse);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book findById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.orElse(null);
    }
}
