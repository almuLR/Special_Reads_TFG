package com.example.special_reads_t.Model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "BOOK")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String author;

    private String genre;
    @Column(length = 5000)
    private String synopsis;

    private int pageCount;

    private String coverImageUrl;

    private String sagaTitle;

    private String isbn;

    private String publisher;

    private String publishedDate;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Review> reviews;

}
