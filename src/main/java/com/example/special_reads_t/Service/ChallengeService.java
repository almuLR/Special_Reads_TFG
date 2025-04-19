package com.example.special_reads_t.Service;


import com.example.special_reads_t.Model.Challenge;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChallengeService {

    @Autowired
    private JournalRepository journalRepository;

    public List<Challenge> getChallengesFor(User user) {
        long booksRead = journalRepository.countByUserAndStatus(user, "Terminado");

        long pagesRead = journalRepository.findByUserAndStatus(user, "Terminado")
                .stream()
                .mapToInt(e ->e.getBook().getPageCount())
                .sum();

        return List.of(
                new Challenge("12 Libros leídos", booksRead, 12),
                new Challenge("100 Libros leídos", booksRead, 100),
                new Challenge("150 Libros leídos", booksRead, 150),
                new Challenge("10.000 páginas", pagesRead, 10000),
                new Challenge("100.000 páginas", pagesRead, 100000),
                new Challenge("150.000 páginas", pagesRead, 150000)
        );
    }
}
