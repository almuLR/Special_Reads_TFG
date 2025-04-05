package com.example.special_reads_t.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "JOURNAL_ENTRY")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int progress;

    private int rating;

    private String status;

    private LocalDateTime startDate;

    private LocalDateTime finishDate;

    @Transient
    private String cssClass;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Transient
    public boolean isFinished() {
        return "Terminado".equalsIgnoreCase(this.status);
    }
    public boolean getFinished() {
        return isFinished();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @Transient
    public boolean isStar1() {
        return rating == 5;
    }

    @Transient
    public boolean isStar2() {
        return rating == 4;
    }

    @Transient
    public boolean isStar3() {
        return rating == 3;
    }

    @Transient
    public boolean isStar4() {
        return rating == 2;
    }

    @Transient
    public boolean isStar5() {
        return rating == 1;
    }


}
