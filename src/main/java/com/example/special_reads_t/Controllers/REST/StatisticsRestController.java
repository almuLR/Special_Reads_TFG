package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.StatisticsService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para estadísticas y datos de gráficos.
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    /**
     * GET /api/statistics/charts
     * Devuelve datos de top autores, géneros, finalizaciones mensuales y métricas.
     */
    @GetMapping("/charts")
    public ResponseEntity<StatisticsResponse> getChartsData() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Datos de servicio
        Map<String, Long> topAuthors = statisticsService.topAuthors(currentUser);
        Map<String, Long> topGenres = statisticsService.topGenres(currentUser);
        Map<String, Long> monthly = statisticsService.monthlyFinishes(currentUser);
        long totBooks = statisticsService.totalBooks(currentUser);
        long totPending = statisticsService.pendingBooks(currentUser);
        long totPages = statisticsService.totalPages(currentUser);
        long totReading = statisticsService.totalReading(currentUser);
        long totChallenges = statisticsService.completedChallenges(currentUser);
        long totFriends = statisticsService.totalFriends(currentUser);

        // Transformaciones a DTOs
        List<NameValueDto> authorsList = topAuthors.entrySet().stream()
                .map(e -> new NameValueDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        List<NameValueDto> genresList = topGenres.entrySet().stream()
                .map(e -> new NameValueDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        List<NameValueDto> monthlyList = monthly.entrySet().stream()
                .map(e -> new NameValueDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // Construcción de la respuesta
        StatisticsResponse response = new StatisticsResponse(
                authorsList,
                genresList,
                monthlyList,
                totBooks,
                totPending,
                totPages,
                totReading,
                totChallenges,
                totFriends
        );
        return ResponseEntity.ok(response);
    }

    /**
     * DTO genérico para pares nombre-valor.
     */
    public static class NameValueDto {
        private String name;
        private Long value;
        public NameValueDto(String name, Long value) {
            this.name = name;
            this.value = value;
        }
        public String getName() { return name; }
        public Long getValue() { return value; }
    }

    /**
     * DTO de respuesta con todos los datos.
     */
    public static class StatisticsResponse {
        private List<NameValueDto> topAuthors;
        private List<NameValueDto> topGenres;
        private List<NameValueDto> monthlyFinishes;
        private long totBooks;
        private long totPending;
        private long totPages;
        private long totReading;
        private long totChallenges;
        private long totFriends;

        public StatisticsResponse(
                List<NameValueDto> topAuthors,
                List<NameValueDto> topGenres,
                List<NameValueDto> monthlyFinishes,
                long totBooks,
                long totPending,
                long totPages,
                long totReading,
                long totChallenges,
                long totFriends) {
            this.topAuthors = topAuthors;
            this.topGenres = topGenres;
            this.monthlyFinishes = monthlyFinishes;
            this.totBooks = totBooks;
            this.totPending = totPending;
            this.totPages = totPages;
            this.totReading = totReading;
            this.totChallenges = totChallenges;
            this.totFriends = totFriends;
        }
        public List<NameValueDto> getTopAuthors() { return topAuthors; }
        public List<NameValueDto> getTopGenres() { return topGenres; }
        public List<NameValueDto> getMonthlyFinishes() { return monthlyFinishes; }
        public long getTotBooks() { return totBooks; }
        public long getTotPending() { return totPending; }
        public long getTotPages() { return totPages; }
        public long getTotReading() { return totReading; }
        public long getTotChallenges() { return totChallenges; }
        public long getTotFriends() { return totFriends; }
    }
}
