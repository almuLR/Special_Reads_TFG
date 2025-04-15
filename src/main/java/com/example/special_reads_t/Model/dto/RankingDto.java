package com.example.special_reads_t.Model.dto;

public class RankingDto {
    private String userName;
    private Long booksCount;

    public RankingDto(String userName, Long booksCount) {
        this.userName = userName;
        this.booksCount = booksCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(Long booksCount) {
        this.booksCount = booksCount;
    }
}
