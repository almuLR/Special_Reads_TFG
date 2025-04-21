package com.example.special_reads_t.Integracion;

import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.JournalRepository;
import com.example.special_reads_t.Repository.UserRepository;   // <-- import adicional
import com.example.special_reads_t.Service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class StatisticsIntegrationTest {

    @Autowired
    private UserRepository userRepository;       // <-- añadido
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private StatisticsService stats;

    private User user;

    @BeforeEach
    void init() {
        // 1) Persistimos el User antes de usarlo en entries
        user = new User();
        // setea aquí los campos obligatorios de User si los tienes (username, email…)
        user = userRepository.save(user);

        // 2) Ahora podemos crear y guardar las entradas sin NPE ni transient errors
        journalRepository.saveAll(List.of(
                createEntry(user, "Terminado",
                        LocalDate.of(2025, 3, 10).atStartOfDay(),
                        "Autor X", List.of("Drama"), 120),
                createEntry(user, "Terminado",
                        LocalDate.of(2025, 3, 12).atStartOfDay(),
                        "Autor Y", List.of("Ficción"), 80),
                createEntry(user, "Leyendo",
                        null,
                        "Autor Z", List.of("Thriller"), 200)
        ));
    }

    @Test
    void integración_topAuthors_y_monthlyFinishes() {
        Map<String, Long> topAuthors = stats.topAuthors(user);
        assertThat(topAuthors).containsExactly(
                entry("Autor X", 1L),
                entry("Autor Y", 1L)
        );

        Map<String, Long> meses = stats.monthlyFinishes(user);
        assertThat(meses.get("Marzo")).isEqualTo(2);
    }

    private JournalEntry createEntry(User user,
                                     String status,
                                     LocalDateTime finishDate,
                                     String autor,
                                     List<String> genres,
                                     int pages) {
        JournalEntry e = new JournalEntry();
        e.setUser(user);
        e.setStatus(status);
        e.setFinishDate(finishDate);

        // Creamos y seteamos el Book antes de asociarlo
        Book book = new Book();
        book.setAuthor(autor);
        book.setGenres(genres);
        book.setPageCount(pages);
        e.setBook(book);

        return e;
    }
}
