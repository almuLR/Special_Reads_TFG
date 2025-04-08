package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Controllers.ReviewController;
import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.Review;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Security.jwt.JwtTokenProvider;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.JournalService;
import com.example.special_reads_t.Service.ReviewService;
import com.example.special_reads_t.Service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad en el test
public class ReviewControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    @MockBean
    private JournalService journalService;

    // Se añade este MockBean para satisfacer la dependencia de seguridad, si fuera necesaria.
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void testSaveReviewSuccess() throws Exception {
        // Datos de prueba
        Long bookId = 1L;
        int starRating = 4;
        BigDecimal decimalRating = new BigDecimal("4.5");
        String format = "Digital";
        int plotTwist = 3;
        int spicy = 2;
        int funny = 5;
        String love = "on"; // representa checkbox marcado
        // Los siguientes parámetros se dejarán nulos, simulando que no se marcaron
        String pain = null, anger = null, xd = null, neutral = null;
        String favoriteCharacter = "Alice";
        String pointOfView = "First Person";
        String oneWord = "Awesome";
        String favoriteQuote = "Carpe Diem";
        boolean recommend = true;
        String reviewText = "Great book";

        // Configuramos mocks
        Book book = new Book();
        book.setId(bookId);
        // Otros campos del libro según sea necesario

        User currentUser = new User();
        currentUser.setUsername("testUser");

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setId(10L);
        journalEntry.setBook(book);
        journalEntry.setUser(currentUser);
        journalEntry.setStatus("Leyendo"); // Estado inicial

        when(bookService.findById(bookId)).thenReturn(book);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(journalService.findByBookAndUser(book, currentUser)).thenReturn(journalEntry);
        when(reviewService.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(journalService.updateJournalEntry(any(JournalEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Realizamos la petición POST
        mockMvc.perform(post("/review/save")
                        .param("bookId", bookId.toString())
                        .param("starRating", String.valueOf(starRating))
                        .param("decimalRating", decimalRating.toString())
                        .param("format", format)
                        .param("plotTwist", String.valueOf(plotTwist))
                        .param("spicy", String.valueOf(spicy))
                        .param("funny", String.valueOf(funny))
                        .param("love", love)  // marca la casilla
                        .param("favoriteCharacter", favoriteCharacter)
                        .param("pointOfView", pointOfView)
                        .param("oneWord", oneWord)
                        .param("favoriteQuote", favoriteQuote)
                        .param("recommend", String.valueOf(recommend))
                        .param("reviewText", reviewText)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/journal"));

        // Verificamos que se llame a reviewService.save y journalService.updateJournalEntry
        verify(reviewService, times(1)).save(any(Review.class));
        verify(journalService, times(1)).updateJournalEntry(any(JournalEntry.class));
    }

    @Test
    public void testSaveReviewBookNotFound() throws Exception {
        Long bookId = 1L;
        when(bookService.findById(bookId)).thenReturn(null);

        mockMvc.perform(post("/review/save")
                        .param("bookId", bookId.toString())
                        .param("starRating", "4")
                        .param("decimalRating", "4.5")
                        .param("format", "Digital")
                        .param("plotTwist", "3")
                        .param("spicy", "2")
                        .param("funny", "5")
                        .param("favoriteCharacter", "Alice")
                        .param("pointOfView", "First Person")
                        .param("oneWord", "Awesome")
                        .param("favoriteQuote", "Carpe Diem")
                        .param("recommend", "true")
                        .param("reviewText", "Great book")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/journal"));

        verify(reviewService, never()).save(any(Review.class));
        verify(journalService, never()).updateJournalEntry(any(JournalEntry.class));
    }

    @Test
    public void testSaveReviewUserNotFound() throws Exception {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        when(bookService.findById(bookId)).thenReturn(book);
        when(userService.getCurrentUser()).thenReturn(null);

        mockMvc.perform(post("/review/save")
                        .param("bookId", bookId.toString())
                        .param("starRating", "4")
                        .param("decimalRating", "4.5")
                        .param("format", "Digital")
                        .param("plotTwist", "3")
                        .param("spicy", "2")
                        .param("funny", "5")
                        .param("favoriteCharacter", "Alice")
                        .param("pointOfView", "First Person")
                        .param("oneWord", "Awesome")
                        .param("favoriteQuote", "Carpe Diem")
                        .param("recommend", "true")
                        .param("reviewText", "Great book")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(reviewService, never()).save(any(Review.class));
        verify(journalService, never()).updateJournalEntry(any(JournalEntry.class));
    }
}