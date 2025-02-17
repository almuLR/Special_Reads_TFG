package com.example.special_reads_t.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "BOOK_PROGRESS")
public class BookProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private String status;
    private Integer pageProgress;
    private Double percentageProgress;
}
