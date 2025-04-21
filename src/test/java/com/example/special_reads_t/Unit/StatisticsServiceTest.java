package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.Challenge;
import com.example.special_reads_t.Repository.JournalRepository;
import com.example.special_reads_t.Service.BookService;
import com.example.special_reads_t.Service.ChallengeService;
import com.example.special_reads_t.Service.StatisticsService;
import com.example.special_reads_t.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private JournalRepository journalRepository;
    @Mock
    private ChallengeService challengeService;
    @Mock
    private BookService bookService;
    @Mock
    private UserService userService;

    @InjectMocks
    private StatisticsService stats;

    private User user;
    private JournalEntry e1, e2, e3;

    @BeforeEach
    void setUp() {
        user = new User();

        // Entrada 1
        e1 = new JournalEntry();
        Book b1 = new Book();
        e1.setBook(b1);
        e1.setFinishDate(LocalDate.of(2025, 1, 15).atStartOfDay());
        b1.setAuthor("Autor A");
        b1.setGenres(Arrays.asList("Ficción", "Aventura"));
        b1.setPageCount(100);

        // Entrada 2
        e2 = new JournalEntry();
        Book b2 = new Book();
        e2.setBook(b2);
        e2.setFinishDate(LocalDate.of(2025, 1, 20).atStartOfDay());
        b2.setAuthor("Autor B");
        b2.setGenres(Collections.singletonList("Ficción"));
        b2.setPageCount(200);

        // Entrada 3
        e3 = new JournalEntry();
        Book b3 = new Book();
        e3.setBook(b3);
        e3.setFinishDate(LocalDate.of(2025, 2, 5).atStartOfDay());
        b3.setAuthor("Autor A");
        b3.setGenres(Collections.singletonList("Historia"));
        b3.setPageCount(150);
    }

    @Test
    void topAuthors_debeContarYOrdenarPorFrecuencia() {
        when(journalRepository.findByUserAndStatus(user, "Terminado"))
                .thenReturn(List.of(e1, e2, e3));

        Map<String, Long> top = stats.topAuthors(user);

        assertThat(top).containsExactly(
                entry("Autor A", 2L),
                entry("Autor B", 1L)
        );
    }

    @Test
    void topGenres_debeContarYLimitarA5() {
        when(journalRepository.findByUserAndStatus(user, "Terminado"))
                .thenReturn(List.of(e1, e2, e3));

        Map<String, Long> top = stats.topGenres(user);

        assertThat(top).containsExactly(
                entry("Ficción", 2L),
                entry("Aventura", 1L),
                entry("Historia", 1L)
        );
    }

    @Test
    void monthlyFinishes_debeInicializarMesesYContar() {
        when(journalRepository.findByUserAndStatus(user, "Terminado"))
                .thenReturn(List.of(e1, e2, e3));

        Map<String, Long> meses = stats.monthlyFinishes(user);

        assertThat(meses.get("Enero")).isEqualTo(2);
        assertThat(meses.get("Febrero")).isEqualTo(1);
        assertThat(meses.get("Marzo")).isEqualTo(0);
    }

    @Test
    void counters_debenContarCorrectamente() {
        when(journalRepository.countByUserAndStatus(user, "Terminado")).thenReturn(3L);
        when(journalRepository.countByUserAndStatus(user, "Pendiente")).thenReturn(5L);
        when(journalRepository.findByUserAndStatus(user, "Terminado"))
                .thenReturn(List.of(e1, e2, e3));
        when(journalRepository.findByUserAndStatus(user, "Leyendo"))
                .thenReturn(List.of(e1, e2));

        // Creamos desafíos con setters
        Challenge pending = new Challenge();
        pending.setCurrent(1L);
        pending.setTarget(2);
        Challenge completed = new Challenge();
        completed.setCurrent(2L);
        completed.setTarget(2);

        when(challengeService.getChallengesFor(user))
                .thenReturn(List.of(pending, completed));
        when(userService.getFriendsOf(user))
                .thenReturn(List.of(new User(), new User()));

        assertThat(stats.totalBooks(user)).isEqualTo(3);
        assertThat(stats.pendingBooks(user)).isEqualTo(5);
        assertThat(stats.totalPages(user)).isEqualTo(450);
        assertThat(stats.totalReading(user)).isEqualTo(2);
        assertThat(stats.totalChallenges(user)).isEqualTo(2);
        assertThat(stats.totalFriends(user)).isEqualTo(2);
        assertThat(stats.completedChallenges(user)).isEqualTo(1);
    }
}
