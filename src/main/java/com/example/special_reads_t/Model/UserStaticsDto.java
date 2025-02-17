package com.example.special_reads_t.Model;

import java.util.List;

public class UserStaticsDto {
    private int totalPagesRead;
    private int totalChaptersRead;
    private int totalBooksRead;
    private int totalPendingBooks;
    private int totalChallengesCompleted;
    private int totalFriends;
    private List<Object[]> topAuthors;
    private List<Object[]> topGenres;


    public UserStaticsDto(int totalPagesRead, int totalChaptersRead, int totalBooksRead,
                          int totalPendingBooks, int totalChallengesCompleted, int totalFriends,
                          List<Object[]> topAuthors, List<Object[]> topGenres) {
        this.totalPagesRead = totalPagesRead;
        this.totalChaptersRead = totalChaptersRead;
        this.totalBooksRead = totalBooksRead;
        this.totalPendingBooks = totalPendingBooks;
        this.totalChallengesCompleted = totalChallengesCompleted;
        this.totalFriends = totalFriends;
        this.topAuthors = topAuthors;
        this.topGenres = topGenres;
    }

    // Getters y Setters
}
