package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.dto.GoogleBooksResponse;
import com.example.special_reads_t.Model.dto.VolumeInfo;
import com.example.special_reads_t.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Book> SearchBooksFromApi(String title) {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title;

        ResponseEntity<GoogleBooksResponse> response = restTemplate.getForEntity(apiUrl, GoogleBooksResponse.class);
        GoogleBooksResponse googleBooksResponse = response.getBody();

        if (googleBooksResponse != null && googleBooksResponse.getResults() != null) {
            return googleBooksResponse.getResults().stream().map(result -> {
                VolumeInfo info = result.getVolumeInfo();
                Book book = new Book();
                book.setTitle(info.getTitle());
                if (info.getAuthors() != null && !info.getAuthors().isEmpty()) {
                    book.setAuthor(info.getAuthors().get(0));
                }
                book.setPublisher(info.getPublisher());
                book.setPublishedDate(info.getPublisherDate());
                book.setSynopsis(info.getDescription());
                book.setPageCount(info.getTotalPages());
                if (info.getImageLinks() != null) {
                    book.setCoverImageUrl(info.getImageLinks().getThumbnail());
                } else {
                    book.setCoverImageUrl("/images/alasNegras.jpeg");
                }

                return book;
            }).collect(Collectors.toList());


        }

        return Collections.emptyList();
    }

    public void testApiResponse(String title) {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title;
        String jsonResponse = restTemplate.getForObject(apiUrl, String.class);
        System.out.println("Respuesta cruda de la API: " + jsonResponse);
    }
}
