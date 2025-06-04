package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    //chart1
    public Map<String, Long> topAuthors(User user) {
        return journalRepository.findByUserAndStatus(user, "Terminado").stream()
                .map(e -> e.getBook().getAuthor())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));
    }

    //chart2
    public Map<String, Long> topGenres(User user) {
        return journalRepository.findByUserAndStatus(user, "Terminado").stream()
                .flatMap(e -> e.getBook().getGenres().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String,Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a,b)->a, LinkedHashMap::new
                ));
    }

    //chart3
    public Map<String, Long> monthlyFinishes(User user) {
        Map<String, Long> meses = new LinkedHashMap<>();
        String[] labels = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        for (String m : labels) {
            meses.put(m, 0L);
        }

        journalRepository.findByUserAndStatus(user, "Terminado").forEach(entry -> {
            if (entry.getFinishDate() != null) {
                int month = entry.getFinishDate().getMonthValue();
                String label = labels[month - 1];
                meses.put(label, meses.get(label) + 1);
            }
        });

        return meses;
    }

    //counters
    public long totalBooks(User user) {
        return journalRepository.countByUserAndStatus(user, "Terminado");
    }
    public long pendingBooks(User user) {
        return journalRepository.countByUserAndStatus(user, "Pendiente"); // o tu etiqueta
    }
    public long totalPages(User user) {
        return journalRepository.findByUserAndStatus(user, "Terminado").stream()
                .mapToInt(e->e.getBook().getPageCount()).sum();
    }
    public long totalReading(User user) {
        return journalRepository.findByUserAndStatus(user, "Leyendo").size();
    }
    public long totalChallenges(User user) {
        return challengeService.getChallengesFor(user).size();
    }
    public long totalFriends(User user) {
        return userService.getFriendsOf(user).size();
    }

    public long completedChallenges(User user) {
        return challengeService.getChallengesFor(user).stream()
                .filter(c -> c.getCurrent() >= c.getTarget())
                .count();
    }
}
