package com.example.special_reads_t.Model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REVIEW")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private String favoriteCharacter;
    private String pointOfView;
    private String oneWordDescription;
    private String favoriteQuote;
    private boolean recommended;
    @Column(length = 5000)
    private String reviewText;
    //1..5
    private int rating;
    //puntuacion decimal
    @Column(name = "decimal_arting", precision = 4, scale = 2)
    private BigDecimal decimalRating;
    private LocalDateTime createdAt;

    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getFavoriteCharacter() {
        return favoriteCharacter;
    }

    public void setFavoriteCharacter(String favoriteCharacter) {
        this.favoriteCharacter = favoriteCharacter;
    }

    public String getPointOfView() {
        return pointOfView;
    }

    public void setPointOfView(String pointOfView) {
        this.pointOfView = pointOfView;
    }

    public String getOneWordDescription() {
        return oneWordDescription;
    }

    public void setOneWordDescription(String oneWordDescription) {
        this.oneWordDescription = oneWordDescription;
    }

    public String getFavoriteQuote() {
        return favoriteQuote;
    }

    public void setFavoriteQuote(String favoriteQuote) {
        this.favoriteQuote = favoriteQuote;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public BigDecimal getDecimalRating() {
        return decimalRating;
    }

    public void setDecimalRating(BigDecimal decimalRating) {
        this.decimalRating = decimalRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
