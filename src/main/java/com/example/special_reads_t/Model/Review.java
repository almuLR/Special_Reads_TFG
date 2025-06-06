package com.example.special_reads_t.Model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String oneWord;
    private String favoriteQuote;
    private boolean recommend;
    @Column(length = 5000)
    private String reviewText;
    //1..5
    private int starRating;
    //puntuacion decimal
    @Column(name = "decimal_arting", precision = 4, scale = 2)
    private BigDecimal decimalRating;
    private String format;
    private int plotTwist;        // 1..5
    private int spicy;            // 1..5
    private int funny;            // 1..5
    private boolean love;
    private boolean pain;
    private boolean anger;
    private boolean xd;
    private boolean neutral;
    private LocalDateTime createdAt;
    @Transient
    private String formattedDate;
    @Transient
    private List<Boolean> filledStars;

    @Transient
    private List<Boolean> emptyStars;

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    @PrePersist
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



    public String getFavoriteQuote() {
        return favoriteQuote;
    }

    public void setFavoriteQuote(String favoriteQuote) {
        this.favoriteQuote = favoriteQuote;
    }



    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
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

    public int getStarRating() {

        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getPlotTwist() {
        return plotTwist;
    }

    public void setPlotTwist(int plotTwist) {
        this.plotTwist = plotTwist;
    }

    public int getSpicy() {
        return spicy;
    }

    public void setSpicy(int spicy) {
        this.spicy = spicy;
    }

    public int getFunny() {
        return funny;
    }

    public void setFunny(int funny) {
        this.funny = funny;
    }

    public boolean isLove() {
        return love;
    }

    public void setLove(boolean love) {
        this.love = love;
    }

    public boolean isPain() {
        return pain;
    }

    public void setPain(boolean pain) {
        this.pain = pain;
    }

    public boolean isAnger() {
        return anger;
    }

    public void setAnger(boolean anger) {
        this.anger = anger;
    }

    public boolean isXd() {
        return xd;
    }

    public void setXd(boolean xd) {
        this.xd = xd;
    }

    public boolean isNeutral() {
        return neutral;
    }

    public void setNeutral(boolean neutral) {
        this.neutral = neutral;
    }

    public String getOneWord() {
        return oneWord;
    }

    public void setOneWord(String oneWord) {
        this.oneWord = oneWord;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }



    public List<Boolean> getFilledStars() {
        return filledStars;
    }

    public void setFilledStars(List<Boolean> filledStars) {
        this.filledStars = filledStars;
    }

    public List<Boolean> getEmptyStars() {
        return emptyStars;
    }

    public void setEmptyStars(List<Boolean> emptyStars) {
        this.emptyStars = emptyStars;
    }

}
