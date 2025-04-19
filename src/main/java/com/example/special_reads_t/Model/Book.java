package com.example.special_reads_t.Model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "BOOK")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String googleBookId;

    private String title;

    private String author;

    private List<String> genres;
    @Column(length = 5000)
    private String synopsis;

    private int pageCount;

    private int chapterCount = 0;

    @Column(name = "cover_image_url", length = 512)
    private String coverImageUrl;

    private String sagaTitle;

    private String isbn;

    private String publisher;

    private String publishedDate;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleBookId() {
        return googleBookId;
    }

    public void setGoogleBookId(String googleBookId) {
        this.googleBookId = googleBookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getChapterCount() {
        return chapterCount;
    }
    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getSagaTitle() {
        return sagaTitle;
    }

    public void setSagaTitle(String sagaTitle) {
        this.sagaTitle = sagaTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
