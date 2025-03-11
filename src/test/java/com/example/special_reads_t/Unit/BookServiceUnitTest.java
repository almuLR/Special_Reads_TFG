package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.dto.GoogleBookResult;
import com.example.special_reads_t.Model.dto.GoogleBooksResponse;
import com.example.special_reads_t.Model.dto.ImageLinks;
import com.example.special_reads_t.Model.dto.VolumeInfo;
import com.example.special_reads_t.Service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookService bookService;

    @Test
    public void testSearchBooksFromApi() {
        // Define el título de búsqueda
        String title = "Test";
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&maxResults=40&startIndex=0";

        // Preparamos los objetos simulados que replican la respuesta de la API
        VolumeInfo volumeInfo = new VolumeInfo();
        volumeInfo.setTitle("Test Book");
        volumeInfo.setAuthors(Arrays.asList("Author1"));
        volumeInfo.setPublisher("Test Publisher");
        volumeInfo.setPublishedDate("2020");
        volumeInfo.setDescription("Test synopsis");
        volumeInfo.setPageCount(100);
        volumeInfo.setCategories(Arrays.asList("Category1"));
        ImageLinks imageLinks = new ImageLinks();
        imageLinks.setThumbnail("http://example.com/thumbnail.jpg");
        volumeInfo.setImageLinks(imageLinks);

        GoogleBookResult googleBookResult = new GoogleBookResult();
        googleBookResult.setGoogleBookId("testId123");
        googleBookResult.setVolumeInfo(volumeInfo);

        GoogleBooksResponse googleBooksResponse = new GoogleBooksResponse();
        googleBooksResponse.setResults(Collections.singletonList(googleBookResult));

        ResponseEntity<GoogleBooksResponse> responseEntity = new ResponseEntity<>(googleBooksResponse, HttpStatus.OK);

        // Configuramos el mock para la llamada GET
        when(restTemplate.getForEntity(apiUrl, GoogleBooksResponse.class))
                .thenReturn(responseEntity);

        // Ejecutamos el método a testear
        List<Book> books = bookService.SearchBooksFromApi(title);

        // Verificamos que se haya convertido correctamente la respuesta
        assertNotNull(books);
        assertEquals(1, books.size());
        Book book = books.get(0);
        assertEquals("testId123", book.getGoogleBookId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Author1", book.getAuthor());
        assertEquals("Test Publisher", book.getPublisher());
        assertEquals("2020", book.getPublishedDate());
        assertEquals("Test synopsis", book.getSynopsis());
        assertEquals(100, book.getPageCount());
        assertEquals(Arrays.asList("Category1"), book.getGenres());
        assertEquals("http://example.com/thumbnail.jpg", book.getCoverImageUrl());
    }
}
