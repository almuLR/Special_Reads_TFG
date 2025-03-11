package com.example.special_reads_t.Integracion;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        // Obtenemos el RestTemplate inyectado en BookService
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(bookService, "restTemplate");
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void testSearchBooksFromApi_Integration() throws Exception {
        String title = "Test";
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&maxResults=40&startIndex=0";

        // JSON de respuesta simulado (se usa "items" en lugar de "results" para imitar la respuesta real de Google Books API)
        String jsonResponse = "{\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"id\": \"testId123\",\n" +
                "      \"volumeInfo\": {\n" +
                "        \"title\": \"Test Book\",\n" +
                "        \"authors\": [\"Author1\"],\n" +
                "        \"publisher\": \"Test Publisher\",\n" +
                "        \"publishedDate\": \"2020\",\n" +
                "        \"description\": \"Test synopsis\",\n" +
                "        \"pageCount\": 100,\n" +
                "        \"categories\": [\"Category1\"],\n" +
                "        \"imageLinks\": {\n" +
                "          \"thumbnail\": \"http://example.com/thumbnail.jpg\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Esperamos una llamada GET a la URL construida
        mockServer.expect(ExpectedCount.once(), requestTo(apiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // Ejecutamos el método de búsqueda
        List<Book> books = bookService.SearchBooksFromApi(title);

        // Verificamos el resultado
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
        assertEquals(List.of("Category1"), book.getGenres());
        assertEquals("http://example.com/thumbnail.jpg", book.getCoverImageUrl());

        mockServer.verify();
    }
}
